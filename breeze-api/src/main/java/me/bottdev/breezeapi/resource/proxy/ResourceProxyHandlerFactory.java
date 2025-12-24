package me.bottdev.breezeapi.resource.proxy;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.di.proxy.ProxyHandler;
import me.bottdev.breezeapi.di.proxy.ProxyHandlerFactory;
import me.bottdev.breezeapi.resource.source.ResourceSourceRegistry;
import me.bottdev.breezeapi.resource.watcher.ResourceWatcher;

@RequiredArgsConstructor
public class ResourceProxyHandlerFactory implements ProxyHandlerFactory {

    private final ResourceSourceRegistry resourceSourceRegistry;
    private final ResourceWatcher resourceWatcher;

    @Override
    public boolean supports(Class<?> iface) {
        return ResourceProvider.class.isAssignableFrom(iface);
    }

    @Override
    public ProxyHandler create(Class<?> targetClass) {
        return new ResourceProxyHandler(resourceSourceRegistry, resourceWatcher);
    }

}
