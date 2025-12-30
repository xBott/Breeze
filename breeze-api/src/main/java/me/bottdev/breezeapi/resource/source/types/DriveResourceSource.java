package me.bottdev.breezeapi.resource.source.types;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.commons.file.FileCommons;
import me.bottdev.breezeapi.commons.file.temp.TempFile;
import me.bottdev.breezeapi.commons.file.temp.TempFiles;
import me.bottdev.breezeapi.commons.file.input.BreezeFileReader;
import me.bottdev.breezeapi.commons.file.output.BreezeFileWriter;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezeapi.resource.ResourceTree;
import me.bottdev.breezeapi.resource.annotations.sources.DriveSource;
import me.bottdev.breezeapi.resource.source.ResourceSource;
import me.bottdev.breezeapi.resource.source.SourceType;
import me.bottdev.breezeapi.resource.types.FileResource;
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

    private final BreezeLogger logger = new SimpleLogger("DriveSource");

    private final Path enginePath;

    @Override
    public ResourceTree<FileResource> provide(Method method) {

        ResourceTree<FileResource> resourceTree = new ResourceTree<>();

        if (!method.isAnnotationPresent(DriveSource.class)) return resourceTree;
        DriveSource annotation = method.getAnnotation(DriveSource.class);
        String pathString = (annotation.path());
        boolean absolute = annotation.absolute();
        boolean createIfAbsent = annotation.createIfAbsent();
        String defaultValue = annotation.defaultValue();

        Path path = absolute ? Path.of(pathString) : enginePath.resolve(pathString);

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
            resourceTree.setRoot(path.getParent());
            createSingleFileResource(path).ifPresent(resource ->
                    resourceTree.add("",  resource)
            );

        } else {

            Map<String, FileResource> resources = createTreeFileResources(path);
            resourceTree.setRoot(path);
            resourceTree.addAll(resources);

        }

        return resourceTree;
    }

    private void createFile(Path path, String defaultValue) throws IOException {
        logger.info("Creating file {}", path);
        File created = FileCommons.createFileOrDirectory(path);

        if (Files.isDirectory(path)) return;
        BreezeFileWriter.INSTANCE.writeString(created, bufferedWriter ->
                bufferedWriter.write(defaultValue)
        );
    }

    private Optional<FileResource> createSingleFileResource(Path path) {

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

    private Map<String, FileResource> createTreeFileResources(Path root) {

        HashMap<String, FileResource> resources = new HashMap<>();

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
