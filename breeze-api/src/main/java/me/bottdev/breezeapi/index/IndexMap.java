package me.bottdev.breezeapi.index;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.commons.dependency.Dependent;
import me.bottdev.breezeapi.index.types.ModuleIndex;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class IndexMap implements Dependent {

    @Getter
    private final ClassLoader classLoader;

    @Override
    public String getDependentId() {
        return getModuleIndex().map(ModuleIndex::getDependentId).orElse("");
    }

    @Override
    public List<String> getDependencies() {
        return getModuleIndex().map(ModuleIndex::getDependencies).orElse(List.of());
    }

    private final HashMap<Class<? extends BreezeIndex>, BreezeIndex> indices = new HashMap<>();

    public int getSize() {
        return indices.size();
    }

    public void put(BreezeIndex index) {
        indices.put(index.getClass(), index);
    }

    public List<BreezeIndex> getIndices() {
        return indices.values().stream().toList();
    }

    @SuppressWarnings("unchecked")
    public <T extends BreezeIndex> Optional<T> get(Class<T> clazz) {
        return Optional.ofNullable((T) indices.get(clazz));
    }

    public Optional<ModuleIndex> getModuleIndex() {
        return get(ModuleIndex.class);
    }

}
