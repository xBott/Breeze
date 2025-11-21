package me.bottdev.breezeapi.autoload;

public interface AutoLoadable<P extends AutoLoadProperties> {

    P getProperties();

    void load();

}
