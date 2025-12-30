package me.bottdev.breezeapi.resource.watcher;

import me.bottdev.breezeapi.commons.Debouncer;
import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.lifecycle.ThreadLifecycle;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.resource.watcher.events.ResourceCreateEvent;
import me.bottdev.breezeapi.resource.watcher.events.ResourceDeleteEvent;
import me.bottdev.breezeapi.resource.watcher.events.ResourceModifyEvent;
import me.bottdev.breezeapi.resource.watcher.events.ResourceWatchEvent;
import me.bottdev.breezeapi.resource.watcher.services.AbstractWatchService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractResourceWatcher<T> extends ThreadLifecycle {

    protected final AbstractWatchService watchService;
    protected final Debouncer<Path> debouncer;
    protected final EventBus eventBus;
    protected final BreezeLogger logger;

    protected final Map<String, ResourceWatchSubject<T>> watchSubjects = new HashMap<>();

    protected AbstractResourceWatcher(
            AbstractWatchService watchService,
            Debouncer<Path> debouncer,
            EventBus eventBus,
            BreezeLogger logger
    ) {
        this.watchService = watchService;
        this.debouncer = debouncer;
        this.logger = logger;
        this.eventBus = eventBus;

        watchService.registerHook(WatchEventType.MODIFY, path ->
                debounce(path, subject ->
                        handleModify(path, subject)
                )
        );
        watchService.registerHook(WatchEventType.CREATE, path ->
                debounce(path, subject ->
                        handleCreate(path, subject)
                )
        );
        watchService.registerHook(WatchEventType.DELETE, path ->
                debounce(path, subject ->
                        handleDelete(path, subject)
                )
        );

    }

    boolean isRegistered(String key) {
        return watchSubjects.containsKey(key);
    }

    boolean isRegistered(ResourceWatchSubject<T> subject) {
        return subject.getRegistrationKey()
                .map(this::isRegistered)
                .orElse(false);
    }

    public void register(ResourceWatchSubject<T> subject) {
        if (isRegistered(subject)) return;
        subject.ifPresent((path, key) -> {

            if (watchService.register(path)) {
                watchSubjects.put(key, subject);
                logger.info("Registered subject \"{}\"", key);
            }

        });
    }

    public void unregister(String key) {
        if (!isRegistered(key)) return;
        watchSubjects.remove(key);
        logger.info("Unregistered subject \"{}\"", key);
    }

    @Override
    protected boolean isDaemon() {
        return true;
    }

    @Override
    protected void onStart() {
        logger.info("Resource Watcher has been started!");
    }

    @Override
    protected void onShutdown() {

        try {
            watchService.close();
        } catch (IOException ex) {
            logger.error("Failed to close single watch service: ", ex);
        }

        debouncer.close();
        watchSubjects.clear();

        logger.info("Resource watcher is shut down.");

    }

    @Override
    protected void threadRun() {

        while (isRunning()) {

            try {
                watchService.watch();

            } catch (InterruptedException e) {
                getThread().interrupt();
                break;
            }

        }

    }

    protected void debounce(Path path, Consumer<ResourceWatchSubject<T>> handler) {
        debouncer.startDebounce(path, debouncedPath ->
                getSubjectByPath(path).ifPresent(handler)
        );
    }

    protected void callWatchEvent(WatchEventType type, ResourceWatchSubject<T> subject) {
        ResourceWatchEvent event = new ResourceWatchEvent(type, subject);
        eventBus.call(event);
    }

    protected void callModifyEvent(Path modifiedPath, ResourceWatchSubject<T> subject) {
        ResourceModifyEvent event = new ResourceModifyEvent(modifiedPath, subject);
        eventBus.call(event);
    }

    protected void callCreateEvent(Path createdPath, ResourceWatchSubject<T> subject) {
        ResourceCreateEvent event = new ResourceCreateEvent(createdPath, subject);
        eventBus.call(event);
    }

    protected void callDeleteEvent(Path deletedPath, ResourceWatchSubject<T> subject) {
        ResourceDeleteEvent event = new ResourceDeleteEvent(deletedPath, subject);
        eventBus.call(event);
    }

    protected abstract Optional<ResourceWatchSubject<T>> getSubjectByPath(Path path);

    protected void handleModify(Path modifiedPath, ResourceWatchSubject<T> watchSubject) {
        callWatchEvent(WatchEventType.MODIFY, watchSubject);
        callModifyEvent(modifiedPath, watchSubject);
    }

    protected void handleCreate(Path createdPath, ResourceWatchSubject<T> watchSubject) {
        callWatchEvent(WatchEventType.CREATE, watchSubject);
        callCreateEvent(createdPath, watchSubject);
    }

    protected void handleDelete(Path deletedPath, ResourceWatchSubject<T> watchSubject) {
        callWatchEvent(WatchEventType.DELETE, watchSubject);
        callDeleteEvent(deletedPath, watchSubject);
    }

}
