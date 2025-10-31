package me.bottdev.breezeapi.index;

import lombok.Getter;
import me.bottdev.breezeapi.index.types.BreezeComponentIndex;
import me.bottdev.breezeapi.index.types.BreezeModuleIndex;
import me.bottdev.breezeapi.index.types.BreezeSupplierIndex;

import java.util.HashMap;

public class BreezeIndexRegistry {

    @Getter
    private final HashMap<Class<? extends BreezeIndex>, String> registeredIndices = new HashMap<>();
    @Getter
    private final BreezeIndexSerializer serializer = new BreezeIndexSerializer();

    public BreezeIndexRegistry() {
        registerDefaults();
    }

    public boolean isRegistered(Class<? extends BreezeIndex> indexClass) {
        return registeredIndices.containsKey(indexClass);
    }

    public void registerIndex(Class<? extends BreezeIndex> indexClass, String id) {
        if (isRegistered(indexClass)) return;
        registeredIndices.put(indexClass, id);
        serializer.registerIndex(indexClass);
    }

    public void registerDefaults() {
        registerIndex(BreezeModuleIndex.class, "module");
        registerIndex(BreezeComponentIndex.class, "component");
        registerIndex(BreezeSupplierIndex.class, "supplier");
    }

}
