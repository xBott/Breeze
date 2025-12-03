package me.bottdev.breezeapi.config;

public interface ConfigProvider<T extends Configuration, C extends ConfigContainer<T>> {

    ConfigLoader getConfigLoader();

    Class<T> getConfigurationClass();

    C provide();

}
