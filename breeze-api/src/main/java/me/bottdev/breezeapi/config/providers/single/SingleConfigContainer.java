package me.bottdev.breezeapi.config.providers.single;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.config.ConfigContainer;
import me.bottdev.breezeapi.config.Configuration;

import java.util.Optional;

@RequiredArgsConstructor
public class SingleConfigContainer<T extends Configuration> implements ConfigContainer<T> {

    public static <T extends Configuration> SingleConfigContainer<T> empty() {
        return new SingleConfigContainer<>(null);
    }

    private final T configuration;

    @Override
    public boolean isEmpty() {
        return configuration == null;
    }

    public Optional<T> get() {
        return Optional.of(configuration);
    }

}
