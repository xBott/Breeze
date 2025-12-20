package me.bottdev.breezeapi.resource.proxy;

import me.bottdev.breezeapi.di.proxy.ProxyHandler;
import me.bottdev.breezeapi.di.proxy.ProxyResult;
import me.bottdev.breezeapi.resource.Resource;
import me.bottdev.breezeapi.resource.ResourceConverter;
import me.bottdev.breezeapi.resource.ResourceTree;
import me.bottdev.breezeapi.resource.annotations.ProvideResource;
import me.bottdev.breezeapi.resource.source.ResourceSource;
import me.bottdev.breezeapi.resource.source.ResourceSourceRegistry;
import me.bottdev.breezeapi.resource.source.SourceType;
import me.bottdev.breezeapi.resource.source.descriptor.SourceDescriptor;
import me.bottdev.breezeapi.resource.source.descriptor.SourceDescriptorFactory;
import me.bottdev.breezeapi.resource.types.FileResource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

public class ResourceProxyHandler implements ProxyHandler {

    private boolean isMethodAnnotated(Method method) {
        return method.isAnnotationPresent(ProvideResource.class);
    }

    @Override
    public boolean supports(Class<?> iface) {
        return ResourceProvider.class.isAssignableFrom(iface);
    }

    @Override
    public ProxyResult invoke(Class<?> targetClass, Object proxy, Method method, Object[] args) throws Throwable {

        if (method.isDefault()) {
            Object value = InvocationHandler.invokeDefault(proxy, method, args);
            return ProxyResult.of(value);
        }

        if (method.isAnnotationPresent(ProvideResource.class)) {

            ProvideResource annotation = method.getAnnotation(ProvideResource.class);
            Class<? extends Resource> type = annotation.type();
            boolean isTree = annotation.isTree();

            Object result;
            result = handleSources(method, type, isTree);

            //return handleResult(method, result);
            return ProxyResult.of(result);
        }

        return ProxyResult.empty();

    }

    private Object handleSources(Method method, Class<? extends Resource> requiredType, boolean isTree) {

        List<SourceDescriptor> descriptors = SourceDescriptorFactory.createFromMethod(method);

        for (SourceDescriptor descriptor : descriptors) {

            SourceType sourceType = descriptor.getType();
            Optional<ResourceSource> sourceOptional = ResourceSourceRegistry.get(sourceType);
            if (sourceOptional.isEmpty()) continue;

            ResourceSource source = sourceOptional.get();
            ResourceTree<FileResource> resourceTree = source.provide(method);
            if (resourceTree.isEmpty()) continue;

            if (isTree) {
                return handleTree(resourceTree, requiredType);
            }

            return handleSingle(resourceTree, requiredType);

        }

        return null;

    }

    private Object handleTree(ResourceTree<FileResource> resourceTree, Class<? extends Resource> requiredType) {
        return ResourceConverter.convertTree(requiredType, resourceTree);
    }

    private Object handleSingle(ResourceTree<FileResource> resourceTree, Class<? extends Resource> requiredType) {
        return resourceTree.getSingle()
                .map(resource ->
                        ResourceConverter.convertSingle(requiredType, resource)
                ).orElse(null);
    }

    private Object handleResult(Method method, Object result) {
        Class<?> returnType = method.getReturnType();

        if (returnType == void.class || returnType == Void.class) {
            return null;
        }

        if (Optional.class.isAssignableFrom(returnType)) {
            if (result == null) {
                return Optional.empty();
            }
            if (result instanceof Optional<?>) {
                return result;
            }
            return Optional.of(result);
        }

        if (ResourceTree.class.isAssignableFrom(returnType)) {
            if (result == null) {
                return new ResourceTree<>();
            }

            if (returnType.isInstance(result)) {
                return result;
            }

            throw new IllegalStateException(
                    "Expected " + returnType.getName() +
                            " but got " + result.getClass().getName()
            );
        }

        if (result == null) {
            return null;
        }

        if (!returnType.isInstance(result)) {
            throw new IllegalStateException(
                    "Return type mismatch: expected " + returnType.getName() +
                            ", got " + result.getClass().getName()
            );
        }

        return result;
    }


    private Object handleEmpty(Method method) {

        Class<?> returnType = method.getReturnType();

        if (returnType == Optional.class) {
            return Optional.empty();
        }

        if (returnType == ResourceTree.class) {
            return new ResourceTree<>();
        }

        if (!returnType.isPrimitive()) {
            return null;
        }

        throw new UnsupportedOperationException(
                "No value for primitive return type: " + returnType.getName()
        );
    }


}
