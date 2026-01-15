package me.bottdev.breezeapi.index;

import lombok.Getter;
import me.bottdev.breezeapi.index.types.ComponentIndex;
import me.bottdev.breezeapi.index.types.ModuleIndex;
import me.bottdev.breezeapi.index.types.FactoryIndex;

import java.util.HashMap;

public class IndexRegistry {

    public static IndexRegistry defaultRegistry() {
        IndexRegistry registry = new IndexRegistry();
        registry.register(ModuleIndex.class, "module");
        registry.register(ComponentIndex.class, "component");
        registry.register(FactoryIndex.class, "supplier");
        return new IndexRegistry();
    }

    @Getter
    private final HashMap<Class<? extends BreezeIndex>, String> registeredIndices = new HashMap<>();
    @Getter
    private final IndexSerializer serializer = new IndexSerializer();

    public boolean isRegistered(Class<? extends BreezeIndex> indexClass) {
        return registeredIndices.containsKey(indexClass);
    }

    public void register(Class<? extends BreezeIndex> indexClass, String id) {
        if (isRegistered(indexClass)) return;
        registeredIndices.put(indexClass, id);
        serializer.registerIndex(indexClass);
    }

}
