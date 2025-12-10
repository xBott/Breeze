package me.bottdev.breezeapi.resource.provide;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.resource.*;
import me.bottdev.breezeapi.resource.annotations.Drive;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class DriveResourceProvideStrategy implements ResourceProvideStrategy {

    private final Path enginePath;

    @Override
    public ResourceChunkContainer provide(Method method) {

        ResourceChunkContainer chunkContainer = new ResourceChunkContainer();

        if (!method.isAnnotationPresent(Drive.class)) return chunkContainer;
        Drive annotation = method.getAnnotation(Drive.class);
        String pathString = annotation.path();

        Path path = Path.of(pathString);


        if (path.toFile().isFile()) {

            provideSingleFile(path).ifPresent(chunkContainer::addChunk);

        } else {

            List<ResourceChunk> chunks = provideTreeFiles(path);
            chunkContainer.addChunks(chunks);

        }

        return chunkContainer;
    }

    private Path replacePathPlaceholders(Path path) {
        String pathString = path.toString();
        String result = pathString
                .replace("\\", "/")
                .replace("{engine}", enginePath.toString());
        return Path.of(result);
    }

    private Optional<ResourceChunk> provideSingleFile(Path path) {
        try {

            byte[] data = Files.readAllBytes(path);

            ResourceMetadata metadata = new ResourceMetadata()
                    .set("path", path);

            ResourceChunk chunk = new ResourceChunk(data, metadata);
            return Optional.of(chunk);

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return Optional.empty();
    }

    private List<ResourceChunk> provideTreeFiles(Path root) {

        List<ResourceChunk> chunks = new ArrayList<>();

        try {

            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @NotNull
                @Override
                public FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) {

                    provideSingleFile(file).ifPresent(chunk -> {

                        String relativePath = root.relativize(file)
                                .toString()
                                .replace("\\", "/");

                        chunk.getMetadata().set("relative", relativePath);

                        chunks.add(chunk);

                    });

                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return chunks;

    }

}
