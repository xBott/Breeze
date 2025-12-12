package me.bottdev.breezeapi.resource;

import me.bottdev.breezeapi.di.proxy.ProxyHandler;
import me.bottdev.breezeapi.resource.annotations.ProvideResource;
import me.bottdev.breezeapi.resource.fallback.Fallback;
import me.bottdev.breezeapi.resource.fallback.ResourceFallbackStrategy;
import me.bottdev.breezeapi.resource.provide.ResourceProvideStrategy;
import me.bottdev.breezeapi.resource.provide.Source;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ResourceProxyHandler implements ProxyHandler {

    private static final Map<Source, ResourceProvideStrategy> provideStrategies = new HashMap<>();
    private static final Map<Fallback, ResourceFallbackStrategy> fallbackStrategies = new HashMap<>();

    public static void registerProvideStrategy(Source source, ResourceProvideStrategy strategy) {
        provideStrategies.put(source, strategy);
    }

    public static Optional<ResourceProvideStrategy> getProvideStrategy(Source source) {
        return Optional.ofNullable(provideStrategies.get(source));
    }

    public static void registerFallbackStrategy(Fallback fallback, ResourceFallbackStrategy fallbackStrategy) {
        fallbackStrategies.put(fallback, fallbackStrategy);
    }

    public static Optional<ResourceFallbackStrategy> getFallbackHandler(Fallback fallback) {
        return Optional.ofNullable(fallbackStrategies.get(fallback));
    }

    @Override
    public boolean supports(Class<?> iface) {
        return ResourceProvider.class.isAssignableFrom(iface);
    }

    @Override
    public Object invoke(Class<?> targetClass, Object proxy, Method method, Object[] args) throws Throwable {

        if (method.isDefault()) {
            return InvocationHandler.invokeDefault(proxy, method, args);
        }

        if (method.isAnnotationPresent(ProvideResource.class)) {

            ProvideResource annotation = method.getAnnotation(ProvideResource.class);
            Source source = annotation.source();
            Class<? extends Resource> type = annotation.type();
            Fallback fallback = annotation.fallback();

            Object result;
            result = handleNotEmpty(method, source, type);

            if (result == null) {
                result = handleFallback(targetClass, proxy, method, fallback, type);
            }

            if (result == null) {
                return handleEmpty(method);
            }

            return handleResult(method, result);
        }

        return handleEmpty(method);

    }

    private Object handleNotEmpty(Method method, Source source, Class<? extends Resource> type) {

        Optional<ResourceProvideStrategy> strategyOptional = getProvideStrategy(source);
        if (strategyOptional.isEmpty()) return null;

        ResourceProvideStrategy strategy = strategyOptional.get();
        ResourceChunkContainer container = strategy.provide(method);

        Optional<? extends Resource> created = ResourceFactory.create(type, container);

        return created.orElse(null);

    }

    private Object handleFallback(Class<?> targetClass, Object proxy, Method method, Fallback fallback, Class<?> requiredType) {
        if (fallback == Fallback.NONE) return null;

        Optional<ResourceFallbackStrategy> strategyOptional = getFallbackHandler(fallback);
        if (strategyOptional.isEmpty()) return null;

        ResourceFallbackStrategy strategy = strategyOptional.get();

        return strategy.fallback(targetClass, proxy, method, requiredType);
    }

    private Object handleResult(Method method, Object result) {

        Class<?> returnType = method.getReturnType();

        if (returnType == Optional.class) {
            return Optional.of(result);
        } else {
            return result;
        }

    }

    private Object handleEmpty(Method method) {

        Class<?> returnType = method.getReturnType();

        if (returnType == Optional.class) {
            return Optional.empty();
        }

        if (!returnType.isPrimitive()) {
            return null;
        }

        throw new UnsupportedOperationException(
                "No value for primitive return type: " + returnType.getName()
        );
    }


}
