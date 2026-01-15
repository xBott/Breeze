package me.bottdev.breezeapi.resource.watcher.services;

import me.bottdev.breezeapi.commons.structures.maps.BiMap;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.resource.watcher.WatchEventType;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public abstract class AbstractWatchService {
    protected final WatchService watchService;
    protected final BiMap<Path, WatchKey> watchKeys = new BiMap<>();
    private final Map<WatchEventType, Consumer<Path>> hooks = new ConcurrentHashMap<>();

    public AbstractWatchService() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
    }

    protected abstract BreezeLogger getLogger();

    public void registerHook(WatchEventType type, Consumer<Path> hook) {
        hooks.put(type, hook);
    }

    protected Optional<Consumer<Path>> getHook(WatchEventType type) {
        return Optional.ofNullable(hooks.get(type));
    }

    protected void useHook(WatchEventType type, Path path) {
        getHook(type).ifPresent(hook -> hook.accept(path));
    }

    public boolean isRegistered(Path directory) {
        return watchKeys.containsKey(directory);
    }

    protected boolean registerWatchDirectory(Path directory) {
        if (!Files.isDirectory(directory)) {
            getLogger().info("\"{}\" is not a directory.", directory);
            return false;
        }

        if (isRegistered(directory)) {
            getLogger().info("\"{}\" is already registered.", directory);
            return false;
        }

        try {

            WatchKey watchKey = directory.register(
                    watchService,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE
            );

            watchKeys.put(directory, watchKey);
            getLogger().info("\"{}\" has been watched.", directory);

            return true;

        } catch (IOException ex) {
            getLogger().error("Failed to register directory \"{}\"", ex, directory);
            return false;
        }
    }

    protected boolean unregisterWatchDirectory(Path directory) {
        if (!isRegistered(directory)) return false;
        WatchKey watchKey = watchKeys.getByKey(directory);
        watchKey.cancel();
        watchKeys.removeByKey(directory);

        getLogger().info("\"{}\" is not watched anymore.", directory);

        return true;
    }

    public abstract boolean register(Path path);

    public abstract boolean unregister(Path path);

    public abstract void reset(Path path);

    public void watch() throws InterruptedException {

        WatchKey key = watchService.take();

        Path directory = watchKeys.getByValue(key);
        if (directory == null) {
            key.reset();
            return;
        }

        handleEvent(directory, key);

        boolean valid = key.reset();
        if (!valid) {
            unregister(directory);
            reset(directory);
        }

    }

    protected void handleEvent(Path directory, WatchKey watchKey) {

        for (WatchEvent<?> event : watchKey.pollEvents()) {

            WatchEvent.Kind<?> kind = event.kind();
            if (kind == StandardWatchEventKinds.OVERFLOW) continue;

            @SuppressWarnings("unchecked")
            WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
            Path actualPath = directory.resolve(pathEvent.context());

            if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                handleModify(actualPath);

            } else if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                handleCreate(actualPath);

            } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                handleDelete(actualPath);

            }

        }

    }

    protected void handleModify(Path modifiedPath) {
        useHook(WatchEventType.MODIFY, modifiedPath);
    }

    protected void handleCreate(Path createdPath) {
        useHook(WatchEventType.CREATE, createdPath);
    }

    protected void handleDelete(Path deletedPath) {
        useHook(WatchEventType.DELETE, deletedPath);
    }

    public void close() throws IOException {
        watchKeys.values().forEach(WatchKey::cancel);
        watchKeys.clear();
        watchService.close();
        hooks.clear();
    }

}
