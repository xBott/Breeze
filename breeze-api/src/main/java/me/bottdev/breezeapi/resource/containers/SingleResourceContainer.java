package me.bottdev.breezeapi.resource.containers;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.resource.Resource;
import me.bottdev.breezeapi.resource.ResourceContainer;

import java.util.Optional;

@RequiredArgsConstructor
public class SingleResourceContainer<T extends Resource> implements ResourceContainer<T> {

    public static <T extends Resource> SingleResourceContainer<T> empty() {
        return new SingleResourceContainer<>(null);
    }

    private final T resource;

    @Override
    public boolean isEmpty() {
        return resource == null;
    }

    @Override
    public Optional<T> get(String key) {
        return Optional.ofNullable(resource);
    }

}
