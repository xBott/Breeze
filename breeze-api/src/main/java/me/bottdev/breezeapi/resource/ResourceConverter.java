package me.bottdev.breezeapi.resource;

import me.bottdev.breezeapi.commons.file.temp.TempFile;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleTreeLogger;
import me.bottdev.breezeapi.resource.source.SourceType;
import me.bottdev.breezeapi.resource.types.FileResource;

import java.lang.reflect.Constructor;
import java.util.Optional;

public class ResourceConverter {

    public static final BreezeLogger logger = new SimpleTreeLogger("ResourceConverter");

    public static <T extends Resource> Optional<T> convertSingle(
            Class<T> clazz,
            FileResource fileResource
    ) {

        try {

            TempFile tempFile = fileResource.getTempFile();
            SourceType sourceType = fileResource.getSourceType();

            Constructor<T> constructor = clazz.getDeclaredConstructor(TempFile.class, SourceType.class);
            constructor.setAccessible(true);
            T instance = constructor.newInstance(tempFile, sourceType);

            return Optional.of(instance);

        } catch (Exception ex) {
            logger.error("Could not convert resource", ex);
            return Optional.empty();
        }
    }

    public static <T extends Resource> ResourceTree<T> convertTree(
            Class<T> clazz,
            ResourceTree<FileResource> resourceTree
    ) {

        ResourceTree<T> newResourceTree = new ResourceTree<>();

        resourceTree.getData().forEach((key, value) -> {

            convertSingle(clazz, value).ifPresent(resource -> {
                newResourceTree.add(key, resource);
            });

        });

        return newResourceTree;

    }


}
