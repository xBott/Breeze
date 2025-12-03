package me.bottdev.breezeapi.resource;

public interface ResourceProvider<L extends ResourceLocation> {

    L getLocation();

}
