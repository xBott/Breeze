package me.bottdev.breezeapi.config;

import me.bottdev.breezeapi.resource.ResourceTree;
import me.bottdev.breezeapi.resource.types.FileResource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public interface ConfigLoader<T extends Configuration> {

    Class<T> getTargetClass();

    Optional<T> load(FileResource resource);

    Optional<T> load(String serialized);

    T loadOrDefault(FileResource resource, Supplier<T> supplier);

    T loadOrCreate(FileResource resource, Supplier<T> supplier);

    default Map<String, T> loadAll(ResourceTree<? extends FileResource> resourceTree) {
        Map<String, T> configs = new HashMap<>();

        resourceTree.getData().forEach((key, resource) ->
                load(resource).ifPresent(config ->
                        configs.put(key, config)
                )
        );

        return configs;
    }

    void save(FileResource resource, T configuration);

}
