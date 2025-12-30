package me.bottdev.breezeapi.resource;

import lombok.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
public class ResourceTree<T extends Resource> {

    @Getter
    private final HashMap<String, T> data = new HashMap<>();
    @Setter
    public Path root = null;

    public Optional<Path> getRoot() {
        return Optional.ofNullable(root);
    }

    public int getSize() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public boolean contains(String key) {
        return data.containsKey(key);
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

    public Optional<T> getEndingWith(String ending) {
        return data.keySet().stream()
                .filter(key -> key.endsWith(ending))
                .findFirst()
                .map(data::get);
    }

    public boolean isSingle() {
        return data.size() == 1;
    }

    public Optional<T> getFirst() {
        if (isEmpty()) return Optional.empty();
        T resource = data.values().stream().toList().getFirst();
        return Optional.ofNullable(resource);
    }

}
