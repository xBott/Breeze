package me.bottdev.breezeapi.config.providers.single;

import me.bottdev.breezeapi.config.ConfigLoader;
import me.bottdev.breezeapi.config.ConfigProvider;
import me.bottdev.breezeapi.config.Configuration;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public interface SingleConfigProvider<T extends Configuration> extends ConfigProvider<T, SingleConfigContainer<T>> {

    Path getPath();

    @Override
    default SingleConfigContainer<T> provide() {

        ConfigLoader loader = getConfigLoader();
        Class<T> configurationClass = getConfigurationClass();

        Path path = getPath();
        File file = path.toFile();

        Optional<T> configurationOptional = loader.loadConfig(file, configurationClass);
        if (configurationOptional.isEmpty()) return SingleConfigContainer.empty();
        T configuration = configurationOptional.get();

        return new SingleConfigContainer<>(configuration);
    }

}
