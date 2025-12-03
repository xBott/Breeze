package me.bottdev.breezeapi.resource;

import java.util.Optional;

public interface ResourceContainer<T extends Resource> {

    boolean isEmpty();

    Optional<T> get(String key);

    default Optional<T> get() {
        return get(null);
    }

}
