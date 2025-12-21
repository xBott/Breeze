package me.bottdev.breezeapi.cache.proxy;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.cache.CacheManager;
import me.bottdev.breezeapi.di.proxy.ProxyHandler;
import me.bottdev.breezeapi.di.proxy.ProxyHandlerFactory;

@RequiredArgsConstructor
public class CacheProxyHandlerFactory implements ProxyHandlerFactory {

    private final CacheManager cacheManager;

    @Override
    public boolean supports(Class<?> iface) {
        return Cacheable.class.isAssignableFrom(iface);
    }

    @Override
    public ProxyHandler create(Class<?> targetClass) {
        return new CacheProxyHandler(cacheManager);
    }

}
