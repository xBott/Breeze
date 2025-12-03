package me.bottdev.breezeapi.resource.providers;

import me.bottdev.breezeapi.resource.ResourceContainer;
import me.bottdev.breezeapi.resource.containers.SingleResourceContainer;
import me.bottdev.breezeapi.resource.containers.TreeResourceContainer;
import me.bottdev.breezeapi.resource.locations.FileResourceLocation;
import me.bottdev.breezeapi.resource.types.FileResource;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public interface DriveResourceProvider extends BinaryResourceProvider<FileResource, FileResourceLocation> {

    @Override
    default ResourceContainer<FileResource> provide() {

        FileResourceLocation location = getLocation();
        Path path = location.getPath();

        ResourceContainer<FileResource> resourceContainer;
        if (path.toFile().isFile()) {
            resourceContainer = provideSingleResource(path);
        } else {
            resourceContainer = provideTreeResource(path);
        }

        return resourceContainer;
    }

    private SingleResourceContainer<FileResource> provideSingleResource(Path path) {
        try {

            byte[] data = Files.readAllBytes(getLocation().getPath());
            FileResource resource = new FileResource(path.getFileName().toString(), data);

            return new SingleResourceContainer<>(resource);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return SingleResourceContainer.empty();

    }

    private TreeResourceContainer<FileResource> provideTreeResource(Path root) {

        try {

            TreeResourceContainer<FileResource> resourceContainer = new TreeResourceContainer<>();

            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @NotNull
                @Override
                public FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) throws IOException {

                    byte[] data = Files.readAllBytes(file);

                    String key = root.relativize(file)
                            .toString()
                            .replace("\\", "/");

                    FileResource resource = new FileResource(key, data);

                    resourceContainer.add(key, resource);

                    return FileVisitResult.CONTINUE;
                }
            });

            return resourceContainer;

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return TreeResourceContainer.empty();

    }

}
