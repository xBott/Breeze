package me.bottdev.breezeapi.resource.proxy;

import me.bottdev.breezeapi.cache.CacheManager;
import me.bottdev.breezeapi.di.proxy.ProxyHandler;
import me.bottdev.breezeapi.di.proxy.ProxyHandlerFactory;
import me.bottdev.breezeapi.resource.proxy.types.HotReloadResourceProxyHandler;
import me.bottdev.breezeapi.resource.proxy.types.SimpleResourceProxyHandler;
import me.bottdev.breezeapi.resource.source.ResourceSourceRegistry;
import me.bottdev.breezeapi.resource.watcher.ResourceWatcher;

public class ResourceProxyHandlerFactory implements ProxyHandlerFactory {

    private final ResourceSourceRegistry resourceSourceRegistry;
    private final ResourceWatcher resourceWatcher;
    private final CacheManager cacheManager;

    public ResourceProxyHandlerFactory(
            ResourceSourceRegistry resourceSourceRegistry
    ) {
        this.resourceSourceRegistry = resourceSourceRegistry;
        this.resourceWatcher = null;
        this.cacheManager = null;
    }

    public ResourceProxyHandlerFactory(
            ResourceSourceRegistry resourceSourceRegistry,
            ResourceWatcher resourceWatcher,
            CacheManager cacheManager
    ) {
        this.resourceSourceRegistry = resourceSourceRegistry;
        this.resourceWatcher = resourceWatcher;
        this.cacheManager = cacheManager;
    }

    @Override
    public boolean supports(Class<?> iface) {
        return ResourceProvider.class.isAssignableFrom(iface);
    }

    @Override
    public ProxyHandler create(Class<?> targetClass) {
        boolean hotReload = resourceWatcher != null && cacheManager != null;
        return hotReload ?
                new HotReloadResourceProxyHandler(resourceSourceRegistry, resourceWatcher, cacheManager) :
                new SimpleResourceProxyHandler(resourceSourceRegistry);

    }

}
