package me.bottdev.breezeapi.resource.watcher.services;

import lombok.Getter;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.platforms.SL4JLogPlatform;
import me.bottdev.breezeapi.resource.watcher.WatchEventType;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class RecursiveWatchService extends AbstractWatchService {

    @Getter
    private final BreezeLogger logger = SL4JLogPlatform.getFactory().simple("RecursiveWatchService");
    private final ScheduledExecutorService delayedExecutor;

    public RecursiveWatchService() throws IOException {
        super();
        this.delayedExecutor =
                Executors.newSingleThreadScheduledExecutor(runnable -> {
                    Thread thread = new Thread(runnable, "recursive-watch-service-delayed");
                    thread.setDaemon(true);
                    return thread;
                });
    }

    @Override
    public boolean register(Path path) {
        try {

            if (!Files.exists(path)) return false;

            List<Path> paths;
            try (Stream<Path> stream = Files.walk(path)) {
                paths = stream
                        .filter(Files::isDirectory)
                        .sorted(Comparator.reverseOrder()).toList();
            }

            return paths.stream().allMatch(this::registerWatchDirectory);

        } catch (IOException ex) {
            logger.error("Failed to recursively register directory \"{}\"", ex, path);
            return false;
        }
    }

    @Override
    public boolean unregister(Path path) {

        List<Path> paths = watchKeys.keySet().stream()
                .filter(watchKey -> watchKey.startsWith(path))
                .toList();

        return paths.stream().allMatch(this::unregisterWatchDirectory);

    }

    @Override
    public void reset(Path path) {
        logger.debug("directory \"{}\" reset, retrying registration...", path);
        delayedExecutor.schedule(() -> {
            register(path);
        }, 200, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void handleCreate(Path createdPath) {
        if (Files.isDirectory(createdPath)) {
            register(createdPath);
        }
        useHook(WatchEventType.CREATE, createdPath);
    }

    @Override
    protected void handleDelete(Path deletedPath) {
        if (isRegistered(deletedPath)) {
            unregister(deletedPath);
        }
        useHook(WatchEventType.DELETE, deletedPath);
    }

}
