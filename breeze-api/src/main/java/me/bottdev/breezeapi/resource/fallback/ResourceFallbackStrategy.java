package me.bottdev.breezeapi.resource.fallback;

import java.lang.reflect.Method;

public interface ResourceFallbackStrategy {

    default boolean isCompatibleReturnType(Method fallback, Class<?> requiredType) {
        Class<?> fallbackType = fallback.getReturnType();
        return requiredType.isAssignableFrom(fallbackType);
    }

    Object fallback(Class<?> targetClass, Object proxy, Method method, Class<?> requiredType);

}
