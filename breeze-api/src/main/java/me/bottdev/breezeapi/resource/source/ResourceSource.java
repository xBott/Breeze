package me.bottdev.breezeapi.resource.source;

import me.bottdev.breezeapi.resource.ResourceTree;
import me.bottdev.breezeapi.resource.types.FileResource;

import java.lang.reflect.Method;

public interface ResourceSource {

    ResourceTree<FileResource> provide(Method method);

}
