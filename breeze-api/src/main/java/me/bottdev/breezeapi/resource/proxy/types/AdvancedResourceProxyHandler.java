package me.bottdev.breezeapi.resource.proxy.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.resource.proxy.ResourceProxyHandler;
import me.bottdev.breezeapi.resource.source.ResourceSourceRegistry;
import me.bottdev.breezeapi.resource.watcher.ResourceWatcher;

@RequiredArgsConstructor
public class AdvancedResourceProxyHandler implements ResourceProxyHandler {

    @Getter
    private final ResourceSourceRegistry resourceSourceRegistry;
    private final ResourceWatcher resourceWatcher;

}
