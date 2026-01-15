package me.bottdev.breezeapi.resource.watcher.types;

import me.bottdev.breezeapi.commons.Debouncer;
import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.log.platforms.SLF4JLogPlatform;
import me.bottdev.breezeapi.resource.ResourceTree;
import me.bottdev.breezeapi.resource.types.FileResource;
import me.bottdev.breezeapi.resource.watcher.AbstractResourceWatcher;
import me.bottdev.breezeapi.resource.watcher.ResourceWatchSubject;
import me.bottdev.breezeapi.resource.watcher.services.RecursiveWatchService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class TreeResourceWatcher extends AbstractResourceWatcher<ResourceTree<? extends FileResource>> {

    public TreeResourceWatcher(EventBus eventBus) throws IOException {
        super(
                new RecursiveWatchService(),
                new Debouncer<>("tree-resource-watcher-debouncer", 200),
                eventBus,
                SLF4JLogPlatform.getFactory().simple("TreeResourceWatcher")
        );
    }

    @Override
    protected String getThreadName() {
        return "tree-resource-watcher";
    }

    @Override
    protected Optional<ResourceWatchSubject<ResourceTree<? extends FileResource>>> getSubjectByPath(Path path) {
        if (path.toString().contains(".swp")) return Optional.empty();
        return watchSubjects.values().stream()
                .filter(subject -> {

                    ResourceTree<? extends FileResource> resourceTree = subject.getTarget();

                    Optional<Path> rootOptional = resourceTree.getRoot();
                    if (rootOptional.isEmpty()) return false;

                    Path root = rootOptional.get();
                    return path.startsWith(root);

                })
                .findFirst();
    }

    @Override
    protected void handleModify(Path modifiedPath, ResourceWatchSubject<ResourceTree<? extends FileResource>> watchSubject) {
        if (Files.isDirectory(modifiedPath)) return;
        logger.info("{} has been modified.", modifiedPath);
        updateTempFile(modifiedPath, watchSubject);
        super.handleModify(modifiedPath, watchSubject);
    }

    @Override
    protected void handleCreate(Path createdPath, ResourceWatchSubject<ResourceTree<? extends FileResource>> watchSubject) {
        if (Files.isDirectory(createdPath)) return;

        logger.info("{} has been created.", createdPath);
        updateTempFile(createdPath, watchSubject);
        super.handleCreate(createdPath, watchSubject);
    }

    @Override
    protected void handleDelete(Path deletedPath, ResourceWatchSubject<ResourceTree<? extends FileResource>> watchSubject) {
        logger.info("{} has been deleted.", deletedPath);
        super.handleDelete(deletedPath, watchSubject);
    }

    private void updateTempFile(Path path, ResourceWatchSubject<ResourceTree<? extends FileResource>> watchSubject) {

        ResourceTree<? extends FileResource> resourceTree = watchSubject.getTarget();
        resourceTree.getRoot().ifPresent(root -> {

            Path relativePath = root.relativize(path);

            resourceTree.getEndingWith(relativePath.toString()).ifPresent(resource -> {

                try {
                    resource.getTempFile().update();
                    logger.info("{} has been updated", resource.getName());

                } catch (IOException ex) {
                    logger.error("Failed to update temp file {}", ex, resource.getTempFile());
                }

            });

        });

    }

}
