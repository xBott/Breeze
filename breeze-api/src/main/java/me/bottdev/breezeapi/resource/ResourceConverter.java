package me.bottdev.breezeapi.resource;

import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleTreeLogger;
import me.bottdev.breezeapi.resource.types.file.SingleFileResource;

import java.lang.reflect.Constructor;
import java.util.Optional;

public class ResourceConverter {

    public static final BreezeLogger logger = new SimpleTreeLogger("ResourceConverter");

    public static <T extends Resource> Optional<T> convertSingle(
            Class<T> clazz,
            SingleFileResource singleFileResource
    ) {

        if (clazz == SingleFileResource.class) return Optional.of(clazz.cast(singleFileResource));

        try {

            Constructor<T> constructor = clazz.getDeclaredConstructor(SingleFileResource.class);
            constructor.setAccessible(true);
            T instance = constructor.newInstance(singleFileResource);

            return Optional.of(instance);

        } catch (Exception ex) {
            logger.error("Could not convert resource", ex);
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Resource> ResourceTree<T> convertTree(
            Class<T> clazz,
            ResourceTree<SingleFileResource> resourceTree
    ) {

        if (clazz == SingleFileResource.class) return (ResourceTree<T>) resourceTree;

        ResourceTree<T> newResourceTree = new ResourceTree<>();

        resourceTree.getData().forEach((key, value) -> {

            convertSingle(clazz, value).ifPresent(resource -> {
                newResourceTree.add(key, resource);
            });

        });

        return newResourceTree;

    }


}
