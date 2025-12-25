package me.bottdev.breezeadmin.providers;

import me.bottdev.breezeadmin.config.SettingsConfiguration;
import me.bottdev.breezeapi.cache.proxy.Cacheable;
import me.bottdev.breezeapi.cache.proxy.annotations.CachePut;
import me.bottdev.breezeapi.di.annotations.Proxy;
import me.bottdev.breezeapi.resource.annotations.HotReload;
import me.bottdev.breezeapi.resource.proxy.ResourceProvider;
import me.bottdev.breezeapi.config.ConfigLoader;
import me.bottdev.breezeapi.config.validation.ConfigValidator;
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

    default Optional<SettingsConfiguration> getSettingsConfiguration() {

        ConfigLoader loader = new ConfigLoader(new JsonMapper(), new ConfigValidator());

        return getSettingsResource().flatMap(resource ->
            loader.loadConfig(resource, SettingsConfiguration.class)
        );

    }

}
