package me.bottdev.breezeapi.resource.fallback;

import java.lang.reflect.Method;
import java.util.Optional;

public interface ResourceFallbackStrategy {

    default boolean isCompatibleReturnType(Method original, Method fallback) {

        Class<?> originalType = original.getReturnType();
        Class<?> fallbackType = fallback.getReturnType();

        if (Optional.class.isAssignableFrom(originalType)) {
            return original.getGenericReturnType().equals(fallback.getGenericReturnType());
        }

        return originalType.isAssignableFrom(fallbackType);
    }

    Object fallback(Class<?> targetClass, Object proxy, Method method);

}
