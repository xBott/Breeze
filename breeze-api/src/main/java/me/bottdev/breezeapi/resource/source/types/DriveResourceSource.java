package me.bottdev.breezeapi.resource.source.types;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.commons.file.FileCommons;
import me.bottdev.breezeapi.commons.file.temp.TempFile;
import me.bottdev.breezeapi.commons.file.temp.TempFiles;
import me.bottdev.breezeapi.commons.file.input.BreezeFileReader;
import me.bottdev.breezeapi.commons.file.output.BreezeFileWriter;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleTreeLogger;
import me.bottdev.breezeapi.resource.ResourceTree;
import me.bottdev.breezeapi.resource.annotations.DriveSource;
import me.bottdev.breezeapi.resource.source.ResourceSource;
import me.bottdev.breezeapi.resource.source.SourceType;
import me.bottdev.breezeapi.resource.types.file.SingleFileResource;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class DriveResourceSource implements ResourceSource {

    private final BreezeLogger logger = new SimpleTreeLogger("DriveProvide");

    private final Path enginePath;

    @Override
    public ResourceTree<SingleFileResource> provide(Method method) {

        ResourceTree<SingleFileResource> resourceTree = new ResourceTree<>();

        if (!method.isAnnotationPresent(DriveSource.class)) return resourceTree;
        DriveSource annotation = method.getAnnotation(DriveSource.class);
        String pathString = (annotation.path());
        boolean createIfAbsent = annotation.createIfAbsent();
        String defaultValue = annotation.defaultValue();

        Path path = enginePath.resolve(pathString);

        if (!Files.exists(path)) {
            if (!createIfAbsent) return resourceTree;

            try {
                createFile(path, defaultValue);
            } catch (IOException ex) {
                logger.error("Could not create file", ex);
                return resourceTree;
            }
        }

        if (Files.isRegularFile(path)) {
            createSingleFileResource(path).ifPresent(resource ->
                    resourceTree.add("",  resource)
            );

        } else {

            Map<String, SingleFileResource> resources = createTreeFileResources(path);
            resourceTree.addAll(resources);

        }

        return resourceTree;
    }

    private void createFile(Path path, String defaultValue) throws IOException {
        File created = FileCommons.createFileOrDirectory(path);
        BreezeFileWriter.INSTANCE.writeString(created, bufferedWriter ->
                bufferedWriter.write(defaultValue)
        );
    }

    private Optional<SingleFileResource> createSingleFileResource(Path path) {

        Path relativePath = enginePath.relativize(path);

        Optional<TempFile> targetOptional = TempFiles.create(relativePath);
        if (targetOptional.isEmpty()) return Optional.empty();

        File source = path.toFile();
        TempFile target = targetOptional.get();
        target.setSourcePath(path);

        try {

            BreezeFileWriter.INSTANCE.writeChunks(target.toFile(), out ->
                    BreezeFileReader.INSTANCE.readChunks(source, (data, length) ->
                            out.write(data, 0, length)
                    )
            );

            SingleFileResource resource = new SingleFileResource(target, SourceType.DRIVE);
            return Optional.of(resource);

        } catch (IOException ex) {
            TempFiles.delete(target);
            logger.error("Could not write temp file", ex);
        }

        return Optional.empty();
    }

    private Map<String, SingleFileResource> createTreeFileResources(Path root) {

        HashMap<String, SingleFileResource> resources = new HashMap<>();

        try {

            Files.walkFileTree(root, new SimpleFileVisitor<>() {
                @Override @NotNull
                public FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) {

                    createSingleFileResource(file).ifPresent(resource -> {

                        Path relativePath = enginePath.relativize(file);
                        resources.put(relativePath.toString(), resource);

                    });

                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException ex) {
            logger.error("Could not walk through file tree.", ex);
            resources.values().forEach(resource ->
                    TempFiles.delete(resource.getTempFile())
            );
        }

        return resources;

    }

}
