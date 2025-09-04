package me.bottdev.breezeapi.serialization.polymorphic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolymorphicRegistry {

    private final Map<Class<?>, PolymorphicType<?>> types = new HashMap<>();

    public List<PolymorphicType<?>> getTypes() {
        return new ArrayList<>(types.values());
    }

    @SuppressWarnings("unchecked")
    public <T> PolymorphicType<T> getType(Class<T> clazz) {
        return (PolymorphicType<T>) types.computeIfAbsent(clazz, _ -> new PolymorphicType<>(clazz));
    }

    public <T> void registerType(Class<T> clazz, PolymorphicType<T> type) {
        types.put(clazz, type);
    }

}

