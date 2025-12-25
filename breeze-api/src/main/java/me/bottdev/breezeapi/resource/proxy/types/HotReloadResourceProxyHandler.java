package me.bottdev.breezeapi.resource.proxy.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.cache.Cache;
import me.bottdev.breezeapi.cache.CacheManager;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezeapi.resource.annotations.HotReload;
import me.bottdev.breezeapi.resource.proxy.ResourceProxyHandler;
import me.bottdev.breezeapi.resource.source.ResourceSourceRegistry;
import me.bottdev.breezeapi.resource.types.FileResource;
import me.bottdev.breezeapi.resource.watcher.ResourceWatcher;

import java.lang.reflect.Method;

@RequiredArgsConstructor
public class HotReloadResourceProxyHandler implements ResourceProxyHandler {

    private final BreezeLogger logger =  new SimpleLogger("HotReloadResourceProxyHandler");

    @Getter
    private final ResourceSourceRegistry resourceSourceRegistry;
    @Getter
    private final ResourceWatcher resourceWatcher;
    @Getter
    private final CacheManager cacheManager;

    @Override
    public Object provideResult(Method method) {
        Object result = ResourceProxyHandler.super.provideResult(method);
        registerResultInWatcher(method, result);
        return result;

    }

    private void registerResultInWatcher(Method method, Object result) {
        if (result == null || !method.isAnnotationPresent(HotReload.class)) return;

        if (result instanceof FileResource resource) {

            resourceWatcher.registerResource(resource);
            if (!resourceWatcher.isRegistered(resource)) return;

            resourceWatcher.getHookContainer(resource).add(changedResource ->
                    onChange(method, changedResource)
            );

        }
    }

    private void onChange(Method method, FileResource changedResource) {
        HotReload hotReload = method.getAnnotation(HotReload.class);
        boolean evictCache = hotReload.evictCache();
        if (evictCache) {
            String cacheGroup = hotReload.cacheGroup();
            evictCache(cacheGroup);
        }
        changedResource.readTrimmed().ifPresent(content -> {
            logger.info("Content: \n{}", content);
        });
    }

    private void evictCache(String cacheGroup) {
        cacheManager.get(cacheGroup).ifPresent(Cache::clear);
    }

}
