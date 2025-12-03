package me.bottdev.breezeapi.config.providers.tree;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.config.ConfigContainer;
import me.bottdev.breezeapi.config.Configuration;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TreeConfigContainer<T extends Configuration> implements ConfigContainer<T> {

    public static <T extends Configuration> TreeConfigContainer<T> empty() {
        return new TreeConfigContainer<>();
    }

    @Getter
    private final HashMap<Path, T> configurations = new HashMap<>();

    @Override
    public boolean isEmpty() {
        return configurations.isEmpty();
    }

    public List<T> getList() {
        return configurations.values().stream().toList();
    }

    public Optional<T> byPath(Path path) {
        return Optional.ofNullable(configurations.get(path));
    }

    public void add(Path path, T configuration) {
        configurations.put(path, configuration);
    }

}
