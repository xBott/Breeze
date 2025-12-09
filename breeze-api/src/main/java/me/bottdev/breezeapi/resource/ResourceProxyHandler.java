package me.bottdev.breezeapi.resource;

import me.bottdev.breezeapi.di.proxy.ProxyHandler;
import me.bottdev.breezeapi.resource.annotations.FallbackMethod;
import me.bottdev.breezeapi.resource.annotations.ProvideResource;
import me.bottdev.breezeapi.resource.fallback.ResourceFallbackHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ResourceProxyHandler implements ProxyHandler {

    private static final Map<Source, ResourceProvideStrategy> provideStrategies = new HashMap<>();
    private static final Map<Class<? extends ResourceFallbackHandler>, ResourceFallbackHandler> fallbackHandler = new HashMap<>();

    public static void registerProvideStrategy(Source source, ResourceProvideStrategy strategy) {
        provideStrategies.put(source, strategy);
    }

    public static Optional<ResourceProvideStrategy> getProvideStrategy(Source source) {
        return Optional.ofNullable(provideStrategies.get(source));
    }

    public static void registerFallbackHandler(Class<? extends ResourceFallbackHandler> handler, ResourceFallbackHandler fallback) {
        fallbackHandler.put(handler, fallback);
    }

    public static Optional<ResourceFallbackHandler> getFallbackHandler(Class<? extends ResourceFallbackHandler> handler) {
        return Optional.ofNullable(fallbackHandler.get(handler));
    }

    @Override
    public boolean supports(Class<?> iface) {
        return ResourceProvider.class.isAssignableFrom(iface);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (method.isDefault()) {
            return InvocationHandler.invokeDefault(proxy, method, args);
        }

        if (method.isAnnotationPresent(ProvideResource.class)) {
            return handleNotEmpty(proxy, method);
        }

        return handleEmpty(method);

    }

    private Object handleNotEmpty(Object proxy, Method method) {

        ProvideResource annotation = method.getAnnotation(ProvideResource.class);
        Source source = annotation.source();
        Class<? extends Resource> type = annotation.type();

        Optional<ResourceProvideStrategy> strategyOptional = getProvideStrategy(source);
        if (strategyOptional.isEmpty()) {
            return handleFallback(proxy, method);
        }

        ResourceProvideStrategy strategy = strategyOptional.get();
        ResourceChunkContainer container = strategy.provide(method);

        Optional<? extends Resource> created = ResourceFactory.create(type, container);
        if (created.isEmpty()) {
            return handleFallback(proxy, method);
        }

        return created;
    }


    private boolean hasFallback(Method method) {
        return method.isAnnotationPresent(FallbackMethod.class);
    }

    private boolean isCompatibleReturnType(Method original, Method fallback) {

        Class<?> originalType = original.getReturnType();
        Class<?> fallbackType = fallback.getReturnType();

        if (Optional.class.isAssignableFrom(originalType)) {
            return original.getGenericReturnType().equals(fallback.getGenericReturnType());
        }

        return originalType.isAssignableFrom(fallbackType);
    }

    private Optional<Method> getFallback(Method original) {

        if (!hasFallback(original)) {
            return Optional.empty();
        }

        FallbackMethod annotation = original.getAnnotation(FallbackMethod.class);
        String fallbackName = annotation.name();

        Class<?> iface = original.getDeclaringClass();

        try {
            Method fallback = iface.getMethod(fallbackName);

            if (fallback.getParameterCount() != 0) {
                throw new IllegalStateException(
                        "Fallback method must have no parameters: " + fallbackName
                );
            }

            if (!isCompatibleReturnType(original, fallback)) {
                throw new IllegalStateException(
                        "Fallback return type doesn't match original method: " + fallbackName
                );
            }

            return Optional.of(fallback);

        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }


    private Object handleFallback(Object proxy, Method method) {

        Optional<Method> fallbackOptional = getFallback(method);
        if (fallbackOptional.isEmpty()) {
            return handleEmpty(method);
        }

        try {
            Method fallback = fallbackOptional.get();
            return InvocationHandler.invokeDefault(proxy, fallback, null);
        } catch (Throwable e) {
            throw new RuntimeException("Error invoking fallback", e);
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
