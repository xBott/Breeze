package me.bottdev.breezeadmin.providers;

import me.bottdev.breezeadmin.config.SettingsConfiguration;
import me.bottdev.breezeapi.cache.proxy.Cacheable;
import me.bottdev.breezeapi.cache.proxy.annotations.CachePut;
import me.bottdev.breezeapi.config.validation.patterns.PathEndsPattern;
import me.bottdev.breezeapi.config.validation.rules.RangeRule;
import me.bottdev.breezeapi.config.validation.rules.StructureRule;
import me.bottdev.breezeapi.config.validation.types.RuleConfigValidator;
import me.bottdev.breezeapi.di.annotations.Proxy;
import me.bottdev.breezeapi.resource.annotations.HotReload;
import me.bottdev.breezeapi.resource.proxy.ResourceProvider;
import me.bottdev.breezeapi.config.SimpleConfigLoader;
import me.bottdev.breezeapi.resource.annotations.sources.DriveSource;
import me.bottdev.breezeapi.resource.annotations.ProvideResource;
import me.bottdev.breezeapi.resource.types.file.SingleFileResource;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;

import java.util.Optional;

@Proxy
public interface SettingsProvider extends ResourceProvider, Cacheable {

    @CachePut(group = "admin_settings", key = "resource", size = 2, ttl = 60_000)
    @ProvideResource
    @DriveSource(path = "modules/Admin/settings.json", defaultValue = "{}")
    @HotReload(eventId = "settings_reload")
    Optional<SingleFileResource> getSettingsResource();

    @CachePut(group = "admin_settings", key = "config", size = 2, ttl = 5_000)
    default Optional<SettingsConfiguration> loadSettingsConfiguration() {

        RuleConfigValidator validator = new RuleConfigValidator();
        validator.getRuleRegistry()
                .addRootRule(new StructureRule(SettingsConfiguration.class))
                .addRule(new PathEndsPattern("version"), new RangeRule(0.0, 1.0));

        SimpleConfigLoader<SettingsConfiguration> loader = new SimpleConfigLoader<>(
                SettingsConfiguration.class,
                new JsonMapper(),
                validator
        );

        return getSettingsResource().flatMap(loader::load);

    }

}
