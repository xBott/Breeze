package me.bottdev.breezeapi.resource.provide;

import me.bottdev.breezeapi.resource.ResourceChunkContainer;

import java.lang.reflect.Method;

public interface ResourceProvideStrategy {

    ResourceChunkContainer provide(Method method);

}
