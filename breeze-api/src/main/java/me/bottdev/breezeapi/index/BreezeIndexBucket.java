package me.bottdev.breezeapi.index;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.dependency.Dependent;
import me.bottdev.breezeapi.index.types.BreezeModuleIndex;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BreezeIndexBucket implements Dependent {

    @Getter
    private final ClassLoader classLoader;

    @Override
    public String getDependentId() {
        return getModuleIndex().map(BreezeModuleIndex::getDependentId).orElse("");
    }

    @Override
    public List<String> getDependencies() {
        return getModuleIndex().map(BreezeModuleIndex::getDependencies).orElse(List.of());
    }

    private final HashMap<Class<? extends BreezeIndex>, BreezeIndex> indices = new HashMap<>();

    public int getSize() {
        return indices.size();
    }

    public void put(BreezeIndex index) {
        indices.put(index.getClass(), index);
    }

    public List<BreezeIndex> getIndices() {
        return indices.values().stream().sorted().collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public <T extends BreezeIndex> Optional<T> get(Class<T> clazz) {
        return Optional.ofNullable((T) indices.get(clazz));
    }

    public Optional<BreezeModuleIndex> getModuleIndex() {
        return get(BreezeModuleIndex.class);
    }

}
