package me.bottdev.breezeadmin;

import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.config.ConfigLoader;
import me.bottdev.breezeapi.config.providers.single.SingleConfigProvider;
import me.bottdev.breezeapi.config.validation.ConfigValidator;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.serialization.Mapper;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;

import java.nio.file.Path;

@Component
public class TestConfigProvider implements SingleConfigProvider<TestConfiguration> {

    private final BreezeEngine engine;

    @Inject
    public TestConfigProvider(BreezeEngine engine) {
        this.engine = engine;
    }

    @Override
    public ConfigLoader getConfigLoader() {
        Mapper mapper = engine.getMapperRegistry().getMapperByClass(JsonMapper.class).orElse(new JsonMapper());
        return new ConfigLoader(
                mapper,
                new ConfigValidator()
        );
    }

    @Override
    public Class<TestConfiguration> getConfigurationClass() {
        return TestConfiguration.class;
    }

    @Override
    public Path getPath() {
        return Path.of("{module}/settings.json");
    }

}
