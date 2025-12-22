package me.bottdev.breezeapi.resource;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ResourceTree<T extends Resource> {

    @Getter
    private final HashMap<String, T> data = new HashMap<>();

    public int getSize() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public ResourceTree<T> add(String key, T resource) {
        data.put(key, resource);
        return this;
    }

    public ResourceTree<T> addAll(Map<String, T> resources) {
        resources.forEach(this::add);
        return this;
    }

    public Optional<T> get(String key) {
        return Optional.ofNullable(data.get(key));
    }

    public boolean isSingle() {
        return data.size() == 1;
    }

    public Optional<T> getSingle() {
        if (!isSingle()) return Optional.empty();
        T resource = data.values().stream().toList().getFirst();
        return Optional.ofNullable(resource);
    }

}
