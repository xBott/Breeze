package me.bottdev.breezeapi.resource.proxy.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.resource.proxy.ResourceProxyHandler;
import me.bottdev.breezeapi.resource.source.ResourceSourceRegistry;

@RequiredArgsConstructor
public class SimpleResourceProxyHandler implements ResourceProxyHandler {

    @Getter
    private final ResourceSourceRegistry resourceSourceRegistry;

}
