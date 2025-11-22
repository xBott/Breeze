package me.bottdev.breezeapi.di;

@FunctionalInterface
public interface ConstructHook {

    void accept(Object object);

}
