package me.bottdev.breezeapi.resource.watcher.types;

import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezeapi.resource.watcher.AbstractResourceWatcher;
import me.bottdev.breezeapi.resource.types.FileResource;
import me.bottdev.breezeapi.commons.Debouncer;
import me.bottdev.breezeapi.resource.watcher.ResourceWatchSubject;
import me.bottdev.breezeapi.resource.watcher.services.SingleWatchService;

import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;

public class SingleResourceWatcher extends AbstractResourceWatcher<FileResource> {

    public SingleResourceWatcher(EventBus eventBus) throws IOException {
        super(
                new SingleWatchService(),
                new Debouncer<>("single-resource-watcher-debouncer", 200),
                eventBus,
                new SimpleLogger("SingleResourceWatcher")
        );
    }

    @Override
    protected String getThreadName() {
        return "single-resource-watcher";
    }

    @Override
    protected Optional<ResourceWatchSubject<FileResource>> getSubjectByPath(Path path) {
        return Optional.ofNullable(watchSubjects.get(path.toString()));
    }

    @Override
    protected void handleModify(Path modifiedPath, ResourceWatchSubject<FileResource> watchSubject) {
        logger.info("{} has been modified.", modifiedPath);
        updateTempFile(watchSubject);
        super.handleModify(modifiedPath, watchSubject);
    }

    @Override
    protected void handleCreate(Path createdPath, ResourceWatchSubject<FileResource> watchSubject) {
        logger.info("{} has been created.", createdPath);
        updateTempFile(watchSubject);
        super.handleCreate(createdPath, watchSubject);
    }

    @Override
    protected void handleDelete(Path deletedPath, ResourceWatchSubject<FileResource> watchSubject) {
        logger.info("{} has been deleted.", deletedPath);
        super.handleDelete(deletedPath, watchSubject);
    }

    private void updateTempFile(ResourceWatchSubject<FileResource> watchSubject) {

        FileResource resource = watchSubject.getTarget();

        try {
            resource.getTempFile().update();
            logger.info("{} has been updated", resource.getName());

        } catch (IOException ex) {
            logger.error("Failed to update temp file {}", ex, resource.getTempFile());
        }

    }

}
