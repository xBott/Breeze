package me.bottdev.breezeapi.commons;

import java.util.Map;

public class PrimitiveWrappers {

    private static final Map<Class<?>, Class<?>> primitiveWrappers = Map.of(
            int.class, Integer.class,
            long.class, Long.class,
            boolean.class, Boolean.class,
            double.class, Double.class,
            float.class, Float.class,
            short.class, Short.class,
            byte.class, Byte.class,
            char.class, Character.class
    );

    public static Class<?> get(Class<?> primitiveType) {
        return primitiveWrappers.getOrDefault(primitiveType, primitiveType);
    }

}
