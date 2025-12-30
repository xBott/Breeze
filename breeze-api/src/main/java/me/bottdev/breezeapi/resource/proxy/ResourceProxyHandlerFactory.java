package me.bottdev.breezeapi.resource.proxy;

import me.bottdev.breezeapi.di.proxy.ProxyHandler;
import me.bottdev.breezeapi.di.proxy.ProxyHandlerFactory;
import me.bottdev.breezeapi.resource.proxy.types.HotReloadResourceProxyHandler;
import me.bottdev.breezeapi.resource.proxy.types.SimpleResourceProxyHandler;
import me.bottdev.breezeapi.resource.source.ResourceSourceRegistry;
import me.bottdev.breezeapi.resource.watcher.types.SingleResourceWatcher;
import me.bottdev.breezeapi.resource.watcher.types.TreeResourceWatcher;

public class ResourceProxyHandlerFactory implements ProxyHandlerFactory {

    private final ResourceSourceRegistry resourceSourceRegistry;
    private final SingleResourceWatcher singleResourceWatcher;
    private final TreeResourceWatcher treeResourceWatcher;

    public ResourceProxyHandlerFactory(
            ResourceSourceRegistry resourceSourceRegistry
    ) {
        this.resourceSourceRegistry = resourceSourceRegistry;
        this.singleResourceWatcher = null;
        this.treeResourceWatcher = null;
    }

    public ResourceProxyHandlerFactory(
            ResourceSourceRegistry resourceSourceRegistry,
            SingleResourceWatcher singleResourceWatcher,
            TreeResourceWatcher treeResourceWatcher
    ) {
        this.resourceSourceRegistry = resourceSourceRegistry;
        this.singleResourceWatcher = singleResourceWatcher;
        this.treeResourceWatcher = treeResourceWatcher;
    }

    @Override
    public boolean supports(Class<?> iface) {
        return ResourceProvider.class.isAssignableFrom(iface);
    }

    @Override
    public ProxyHandler create(Class<?> targetClass) {
        boolean hotReload = singleResourceWatcher != null && treeResourceWatcher != null;
        return hotReload ? createHotReload() : createSimple();

    }

    private ProxyHandler createSimple() {
        return new SimpleResourceProxyHandler(resourceSourceRegistry);
    }

    private ProxyHandler createHotReload() {
        return new HotReloadResourceProxyHandler(resourceSourceRegistry, singleResourceWatcher, treeResourceWatcher);
    }

}
