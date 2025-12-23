package me.bottdev.breezeadmin.providers;

import me.bottdev.breezeadmin.config.SettingsConfiguration;
import me.bottdev.breezeapi.cache.proxy.Cacheable;
import me.bottdev.breezeapi.cache.proxy.annotations.CachePut;
import me.bottdev.breezeapi.di.annotations.Proxy;
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

    @CachePut
    @ProvideResource
    @DriveSource(path = "Admin/settings.json", defaultValue = "{}")
    Optional<SingleFileResource> getSettingsResource();
    //ДОБАВИТЬ СОХРАНЕНИЕ РЕСУРСОВ
    //ДОБАВИТЬ КЕШ (Ресурсы и конфиги раздельно)
    //ДОБАВИТЬ ПЕРЕЗАГРУЗКУ РЕСУРСОВ И КОНФИГОВ (WatcherService + CheckSum?)

    default Optional<SettingsConfiguration> getSettingsConfiguration() {

        ConfigLoader loader = new ConfigLoader(new JsonMapper(), new ConfigValidator());

        getSettingsResource().ifPresent(resource -> resource.writeAndSave("{}"));

        return getSettingsResource().map(resource ->
            loader.loadConfig(resource, SettingsConfiguration.class).get()
        );

    }

}
