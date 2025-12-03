package me.bottdev.breezeapi.resource.containers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.resource.Resource;
import me.bottdev.breezeapi.resource.ResourceContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TreeResourceContainer<T extends Resource> implements ResourceContainer<T> {

    public static <T extends Resource> TreeResourceContainer<T> empty() {
        return new TreeResourceContainer<>();
    }

    @Getter
    private final HashMap<String, T> resources = new HashMap<>();

    @Override
    public boolean isEmpty() {
        return resources.isEmpty();
    }

    @Override
    public Optional<T> get(String key) {
        return Optional.ofNullable(resources.get(key));
    }

    public List<T> getList() {
        return resources.values().stream().toList();
    }

    public void add(String key, T configuration) {
        resources.put(key, configuration);
    }

}
