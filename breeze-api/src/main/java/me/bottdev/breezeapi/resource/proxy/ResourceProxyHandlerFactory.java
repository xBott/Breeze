package me.bottdev.breezeapi.resource.proxy;

import me.bottdev.breezeapi.di.proxy.ProxyHandler;
import me.bottdev.breezeapi.di.proxy.ProxyHandlerFactory;
import me.bottdev.breezeapi.resource.proxy.types.AdvancedResourceProxyHandler;
import me.bottdev.breezeapi.resource.proxy.types.SimpleResourceProxyHandler;
import me.bottdev.breezeapi.resource.source.ResourceSourceRegistry;
import me.bottdev.breezeapi.resource.watcher.ResourceWatcher;

public class ResourceProxyHandlerFactory implements ProxyHandlerFactory {

    private final ResourceSourceRegistry resourceSourceRegistry;
    private final ResourceWatcher resourceWatcher;

    public ResourceProxyHandlerFactory(
            ResourceSourceRegistry resourceSourceRegistry
    ) {
        this.resourceSourceRegistry = resourceSourceRegistry;
        this.resourceWatcher = null;
    }

    public ResourceProxyHandlerFactory(
            ResourceSourceRegistry resourceSourceRegistry,
            ResourceWatcher resourceWatcher
    ) {
        this.resourceSourceRegistry = resourceSourceRegistry;
        this.resourceWatcher = resourceWatcher;
    }

    @Override
    public boolean supports(Class<?> iface) {
        return ResourceProvider.class.isAssignableFrom(iface);
    }

    @Override
    public ProxyHandler create(Class<?> targetClass) {
        return resourceWatcher == null ?
                new SimpleResourceProxyHandler(resourceSourceRegistry) :
                new AdvancedResourceProxyHandler(resourceSourceRegistry, resourceWatcher);
    }

}
