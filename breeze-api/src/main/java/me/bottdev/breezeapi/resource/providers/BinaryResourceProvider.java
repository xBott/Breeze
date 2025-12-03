package me.bottdev.breezeapi.resource.providers;

import me.bottdev.breezeapi.resource.BinaryResource;
import me.bottdev.breezeapi.resource.ResourceContainer;
import me.bottdev.breezeapi.resource.ResourceLocation;
import me.bottdev.breezeapi.resource.ResourceProvider;

public interface BinaryResourceProvider<T extends BinaryResource, L extends ResourceLocation> extends ResourceProvider<L> {

    ResourceContainer<T> provide();

}
