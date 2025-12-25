package me.bottdev.breezeapi.resource.proxy.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.resource.annotations.HotReload;
import me.bottdev.breezeapi.resource.proxy.ResourceProxyHandler;
import me.bottdev.breezeapi.resource.source.ResourceSourceRegistry;
import me.bottdev.breezeapi.resource.types.FileResource;
import me.bottdev.breezeapi.resource.watcher.ResourceWatcher;

import java.lang.reflect.Method;

@RequiredArgsConstructor
public class HotReloadResourceProxyHandler implements ResourceProxyHandler {

    @Getter
    private final ResourceSourceRegistry resourceSourceRegistry;
    private final ResourceWatcher resourceWatcher;

    @Override
    public Object provideResult(Method method) {
        Object result = ResourceProxyHandler.super.provideResult(method);
        registerResultInWatcher(method, result);
        return result;

    }

    private void registerResultInWatcher(Method method, Object result) {
        if (result == null || !method.isAnnotationPresent(HotReload.class)) return;
        HotReload hotReload = method.getAnnotation(HotReload.class);
        String eventId = hotReload.eventId();

        if (result instanceof FileResource resource) {
            resourceWatcher.registerResource(resource, eventId);

        }
    }


}
