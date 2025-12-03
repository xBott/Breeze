package me.bottdev.breezeapi.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.config.validation.ConfigValidator;
import me.bottdev.breezeapi.log.TreeLogger;

import java.util.HashMap;

@RequiredArgsConstructor
public class ConfigLoaderRegistry {

    public final TreeLogger logger;

    @Getter
    private final ConfigValidator configValidator;

    private final HashMap<String, ConfigLoader> loaders = new HashMap<>();





}
