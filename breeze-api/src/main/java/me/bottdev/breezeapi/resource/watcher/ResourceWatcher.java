package me.bottdev.breezeapi.resource.watcher;

import me.bottdev.breezeapi.commons.structures.BiMap;
import me.bottdev.breezeapi.commons.file.temp.TempFile;
import me.bottdev.breezeapi.lifecycle.Lifecycle;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezeapi.resource.types.FileResource;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class ResourceWatcher extends Lifecycle {

    private static final long DEBOUNCE_DELAY_MS = 200;

    private final BreezeLogger logger = new SimpleLogger("ResourceWatcher");

    private volatile boolean running = false;
    private final WatchService watchService;
    private Thread watchThread;

    private final ScheduledExecutorService debounceExecutor =
            Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "resource-watcher-debounce");
                t.setDaemon(true);
                return t;
            });
    private final Map<FileResource, ScheduledFuture<?>> debounceTasks = new ConcurrentHashMap<>();


    private final BiMap<WatchKey, Path> watchKeys = new BiMap<>();
    private final BiMap<FileResource, Path> registeredResources = new BiMap<>();
    private final Map<FileResource, WatcherHookContainer> hookContainers = new HashMap<>();


    public ResourceWatcher() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
    }

    public boolean isRegistered(FileResource resource) {
        return registeredResources.containsKey(resource);
    }

    private void registerWatcherDirectory(Path directory) {

        if (!Files.isDirectory(directory)) {
            return;
        }

        if (watchKeys.containsValue(directory)) {
            return;
        }

        try {

            WatchKey key = directory.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE
            );

            watchKeys.put(key, directory);

        } catch (IOException e) {
            throw new RuntimeException("Failed to register directory: " + directory, e);
        }

    }

    public void registerResource(FileResource resource) {

        TempFile tempFile = resource.getTempFile();

        tempFile.getSourceFile().ifPresent(file -> {

                Path path = file.toPath();
                Path parentPath = path.getParent();

                if (registeredResources.containsKey(resource)) {
                    return;
                }

                registeredResources.put(resource, path);
                registerWatcherDirectory(parentPath);

                logger.info("{} has been registered", resource.getName());

        });

    }

    public WatcherHookContainer getHookContainer(FileResource resource) {
        return hookContainers.computeIfAbsent(
                resource,
                ignored -> new WatcherHookContainer()
        );
    }


    @Override
    protected void onStart() {
        if (running) return;
        running = true;

        watchThread = new Thread(this::processEvents, "resource-watcher");
        watchThread.setDaemon(true);
        watchThread.start();
    }

    @Override
    protected void onShutdown() {

        running = false;

        if (watchThread != null) {
            watchThread.interrupt();
        }

        try {
            watchService.close();
        } catch (IOException ignored) {}

        debounceExecutor.shutdown();

        watchKeys.clear();
        registeredResources.clear();
        hookContainers.clear();
        debounceTasks.clear();

    }

    private void processEvents() {

        while (running) {

            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            Path directory = watchKeys.getByKey(key);
            if (directory == null) {
                key.reset();
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                @SuppressWarnings("unchecked")
                WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                Path changedPath = directory.resolve(pathEvent.context());

                if (Files.isDirectory(changedPath)) {
                    continue;
                }

                FileResource resource = registeredResources.getByValue(changedPath);
                if (resource == null) {
                    continue;
                }

                onChange(kind, resource);
            }

            boolean valid = key.reset();
            if (!valid) {
                watchKeys.removeByKey(key);
            }

        }
    }

    private void onChange(WatchEvent.Kind<?> kind, FileResource resource) {

        if (kind != StandardWatchEventKinds.ENTRY_MODIFY) return;

        ScheduledFuture<?> previous = debounceTasks.get(resource);
        if (previous != null) {
            previous.cancel(false);
        }

        ScheduledFuture<?> future = debounceExecutor.schedule(() -> {
            try {
                resource.getTempFile().update();
                logger.info("{} has been changed (debounced)", resource.getName());
                acceptHooks(resource);

            } catch (IOException ex) {
                logger.error("Failed to update temp file {}", ex, resource.getTempFile());
            }
        }, DEBOUNCE_DELAY_MS, TimeUnit.MILLISECONDS);

        debounceTasks.put(resource, future);
    }


    private void acceptHooks(FileResource resource) {
        WatcherHookContainer hookContainer = getHookContainer(resource);
        if (hookContainer == null) return;
        hookContainer.acceptAll(resource);
    }

}
