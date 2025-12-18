package me.bottdev.breezeapi.resource.provide;

import me.bottdev.breezeapi.resource.ResourceTree;
import me.bottdev.breezeapi.resource.types.file.SingleFileResource;

import java.lang.reflect.Method;

public interface ResourceProvideStrategy {

    ResourceTree<SingleFileResource> provide(Method method);

}
