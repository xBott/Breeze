package me.bottdev.breezeapi.resource.source.types;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.commons.file.output.BreezeFileWriter;
import me.bottdev.breezeapi.commons.file.temp.TempFile;
import me.bottdev.breezeapi.commons.file.temp.TempFiles;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleTreeLogger;
import me.bottdev.breezeapi.resource.ResourceTree;
import me.bottdev.breezeapi.resource.annotations.DummySource;
import me.bottdev.breezeapi.resource.source.ResourceSource;
import me.bottdev.breezeapi.resource.source.SourceType;
import me.bottdev.breezeapi.resource.types.FileResource;
import me.bottdev.breezeapi.resource.types.file.SingleFileResource;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Optional;

@RequiredArgsConstructor
public class DummyResourceSource implements ResourceSource {

    private final BreezeLogger logger = new SimpleTreeLogger("DummySource");

    @Override
    public ResourceTree<FileResource> provide(Method method) {

        ResourceTree<FileResource> resourceTree = new ResourceTree<>();

        if (!method.isAnnotationPresent(DummySource.class)) return resourceTree;
        DummySource annotation = method.getAnnotation(DummySource.class);
        String value = annotation.value();

        Path path = Path.of("dummy")
                .resolve(method.getDeclaringClass().getSimpleName())
                .resolve(method.getName() + ".txt");

        createDummyFileResource(path, value).ifPresent(resource ->
                resourceTree.add("", resource)
        );

        return resourceTree;
    }

    private Optional<FileResource> createDummyFileResource(Path path, String value) {

        Optional<TempFile> tempFileOptional = TempFiles.create(path);
        if (tempFileOptional.isEmpty()) return Optional.empty();

        TempFile tempFile = tempFileOptional.get();

        try {

            BreezeFileWriter.INSTANCE.writeString(tempFile.toFile(), bufferedWriter ->
                    bufferedWriter.write(value)
            );

            SingleFileResource resource = new SingleFileResource(tempFile, SourceType.DUMMY);
            return Optional.of(resource);

        } catch (IOException ex) {
            logger.error("Could not write temporary file", ex);
        }

        return Optional.empty();

    }

}
