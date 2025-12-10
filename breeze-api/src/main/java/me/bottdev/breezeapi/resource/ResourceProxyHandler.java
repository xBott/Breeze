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
            return handleNotEmpty(targetClass, proxy, method);
        }

        return handleEmpty(method);

    }

    private Object handleNotEmpty(Class<?> targetClass, Object proxy, Method method) {

        ProvideResource annotation = method.getAnnotation(ProvideResource.class);
        Source source = annotation.source();
        Class<? extends Resource> type = annotation.type();
        Fallback fallback = annotation.fallback();

        Optional<ResourceProvideStrategy> strategyOptional = getProvideStrategy(source);
        if (strategyOptional.isEmpty()) {
            return handleFallback(targetClass, proxy, method, fallback);
        }

        ResourceProvideStrategy strategy = strategyOptional.get();
        ResourceChunkContainer container = strategy.provide(method);

        Optional<? extends Resource> created = ResourceFactory.create(type, container);
        if (created.isEmpty()) {
            return handleFallback(targetClass, proxy, method, fallback);
        }

        return created;
    }

    private Object handleFallback(Class<?> targetClass, Object proxy, Method method, Fallback fallback) {
        if (fallback == Fallback.NONE) return handleEmpty(method);

        Optional<ResourceFallbackStrategy> strategyOptional = getFallbackHandler(fallback);
        if (strategyOptional.isEmpty()) {
            return handleEmpty(method);
        }

        ResourceFallbackStrategy strategy = strategyOptional.get();
        Object result = strategy.fallback(targetClass, proxy, method);

        if (result == null) {
            return handleEmpty(method);
        }

        return result;
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
