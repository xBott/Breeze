package me.bottdev.breezeapi.resource.fallback;

import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleLogger;
import me.bottdev.breezeapi.resource.annotations.FallbackMethod;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;

public class MethodFallbackStrategy implements ResourceFallbackStrategy {

    private final BreezeLogger logger = new SimpleLogger("MethodFallbackStrategy");

    private boolean hasFallback(Method method) {
        return method.isAnnotationPresent(FallbackMethod.class);
    }

    private Optional<Method> getFallbackMethod(Method original) {

        if (!hasFallback(original)) {
            return Optional.empty();
        }

        FallbackMethod annotation = original.getAnnotation(FallbackMethod.class);
        String fallbackName = annotation.name();

        Class<?> iface = original.getDeclaringClass();

        try {
            Method fallback = iface.getMethod(fallbackName);

            if (fallback.getParameterCount() != 0) {
                logger.warn("Fallback method {} has no parameters.", fallbackName);
                return Optional.empty();
            }

            if (!isCompatibleReturnType(original, fallback)) {
                logger.warn("Fallback method {} has wrong return type.", fallbackName);
                return Optional.empty();
            }

            return Optional.of(fallback);

        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    @Override
    public Object fallback(Class<?> targetClass, Object proxy, Method method) {
        Optional<Method> fallbackOptional = getFallbackMethod(method);

        if (fallbackOptional.isEmpty()) return null;

        try {
            Method fallback = fallbackOptional.get();
            return InvocationHandler.invokeDefault(proxy, fallback, (Object) null);

        } catch (Throwable e) {
            logger.warn("Could not invoke fallback method for {}.", method.getName());
        }

        return null;

    }

}
