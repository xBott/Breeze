package me.bottdev.breezeapi.resource.watcher;

import me.bottdev.breezeapi.commons.structures.BiMap;
import me.bottdev.breezeapi.commons.file.temp.TempFile;
import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.lifecycle.ThreadLifecycle;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezeapi.resource.events.ResourceWatchEvent;
import me.bottdev.breezeapi.resource.types.FileResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ResourceWatcher extends ThreadLifecycle {

    private static final long DEBOUNCE_DELAY_MS = 200;

    private final BreezeLogger logger = new SimpleLogger("ResourceWatcher");

    private final WatchService watchService;
    private final EventBus eventBus;

    private final ScheduledExecutorService debounceExecutor =
            Executors.newSingleThreadScheduledExecutor(runnable -> {
                Thread thread = new Thread(runnable, "resource-watcher-debounce");
                thread.setDaemon(true);
                return thread;
            });
    private final Map<ResourceWatchSubject, ScheduledFuture<?>> debounceTasks = new ConcurrentHashMap<>();


    private final BiMap<Path, WatchKey> watchKeys = new BiMap<>();
    private final Map<Path, ResourceWatchSubject> watchSubjects = new HashMap<>();


    public ResourceWatcher(EventBus eventBus) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.eventBus = eventBus;
    }

    public boolean isRegistered(FileResource resource) {
        return resource.getTempFile().getSourceFile()
                .map(File::toPath)
                .map(this::isRegistered)
                .orElse(false);
    }

    public boolean isRegistered(Path path) {
        return watchSubjects.containsKey(path);
    }

    private void registerWatcherDirectory(Path directory) {

        if (!Files.isDirectory(directory) || watchKeys.containsKey(directory)) {
            return;
        }

        try {

            WatchKey watchKey = directory.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE
            );

            watchKeys.put(directory, watchKey);

            logger.info("{} has been registered.", directory);

        } catch (IOException e) {
            throw new RuntimeException("Failed to register directory: " + directory, e);
        }

    }

    public void registerResource(FileResource resource, String eventId) {

        if (isRegistered(resource)) {
            logger.info("{} is already registered.", resource.getName());
            return;
        }

        TempFile tempFile = resource.getTempFile();

        tempFile.getSourceFile().ifPresent(file -> {

                Path sourcePath = file.toPath();
                Path parentDirectory = sourcePath.getParent();

                ResourceWatchSubject watchSubject = new ResourceWatchSubject(resource, eventId);
                watchSubjects.put(sourcePath, watchSubject);
                registerWatcherDirectory(parentDirectory);

                logger.info("{} has been registered.", resource.getName());

        });

    }

    @Override
    protected boolean isDaemon() {
        return true;
    }

    @Override
    protected String getThreadName() {
        return "resource-watcher";
    }

    @Override
    protected void onStart() {
        logger.info("Resource watcher has been started!");
    }

    @Override
    protected void onShutdown() {

        try {
            watchService.close();
        } catch (IOException ex) {
            logger.error("Failed to close watch service: ", ex);
        }

        debounceExecutor.shutdown();
        debounceTasks.clear();

        watchKeys.clear();
        watchSubjects.clear();

        logger.info("Resource watcher is shut down.");

    }

    @Override
    protected void threadRun() {

        while (isRunning()) {

            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }


            Path directory = watchKeys.getByValue(key);
            if (directory == null) {
                key.reset();
                continue;
            }

            handleWatchEvents(directory, key);

            boolean valid = key.reset();
            if (!valid) {
                watchKeys.removeByValue(key);
            }

        }
    }

    private void handleWatchEvents(Path directory, WatchKey watchKey) {

        for (WatchEvent<?> event : watchKey.pollEvents()) {

            WatchEvent.Kind<?> kind = event.kind();

            if (kind == StandardWatchEventKinds.OVERFLOW) continue;

            @SuppressWarnings("unchecked")
            WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
            Path changedPath = directory.resolve(pathEvent.context());

            if (Files.isDirectory(changedPath)) continue;

            ResourceWatchSubject watchSubject = watchSubjects.get(changedPath);
            if (watchSubject == null) continue;

            onChange(watchSubject);
        }

    }

    private void onChange(ResourceWatchSubject watchSubject) {



        ScheduledFuture<?> previous = debounceTasks.get(watchSubject);
        if (previous != null) {
            previous.cancel(false);
        }

        ScheduledFuture<?> future = debounceExecutor.schedule(
                () -> onDebounce(watchSubject),
                DEBOUNCE_DELAY_MS,
                TimeUnit.MILLISECONDS
        );

        debounceTasks.put(watchSubject, future);
    }

    private void onDebounce(ResourceWatchSubject watchSubject) {
        updateTempFile(watchSubject);
        callEvent(watchSubject);
    }

    private void updateTempFile(ResourceWatchSubject watchSubject) {

        FileResource resource = watchSubject.getResource();

        try {

            resource.getTempFile().update();
            logger.info("{} has been changed (debounced)", resource.getName());

        } catch (IOException ex) {
            logger.error("Failed to update temp file {}", ex, resource.getTempFile());
        }

    }

    private void callEvent(ResourceWatchSubject watchSubject) {
        ResourceWatchEvent watchEvent = new ResourceWatchEvent(watchSubject);
        eventBus.call(watchEvent);
    }

}
