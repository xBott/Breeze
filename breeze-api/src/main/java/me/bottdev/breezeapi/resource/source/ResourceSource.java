package me.bottdev.breezeapi.resource.source;

import me.bottdev.breezeapi.resource.ResourceTree;
import me.bottdev.breezeapi.resource.types.file.SingleFileResource;

import java.lang.reflect.Method;

public interface ResourceSource {

    ResourceTree<SingleFileResource> provide(Method method);

}
