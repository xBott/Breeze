package me.bottdev.breezeadmin.providers;

import me.bottdev.breezeadmin.config.SettingsConfiguration;
import me.bottdev.breezeapi.di.annotations.Proxy;
import me.bottdev.breezeapi.resource.ResourceProvider;
import me.bottdev.breezeapi.resource.config.ConfigLoader;
import me.bottdev.breezeapi.resource.config.validation.ConfigValidator;
import me.bottdev.breezeapi.resource.provide.Source;
import me.bottdev.breezeapi.resource.annotations.Drive;
import me.bottdev.breezeapi.resource.annotations.ProvideResource;
import me.bottdev.breezeapi.resource.types.file.SingleFileResource;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;

import java.util.Optional;

@Proxy
public interface SettingsProvider extends ResourceProvider {

    @ProvideResource(source = Source.DRIVE)
    @Drive(path = "Admin/settings.json")
    Optional<SingleFileResource> getSettingsResource();
    //ДОБАВИТЬ СОХРАНЕНИЕ РЕСУРСОВ
    //ДОБАВИТЬ КЕШ (Ресурсы и конфиги раздельно)
    //ДОБАВИТЬ ПЕРЕЗАГРУЗКУ РЕСУРСОВ И КОНФИГОВ (WatcherService + CheckSum?)

    default Optional<SettingsConfiguration> getSettingsConfiguration() {

        ConfigLoader loader = new ConfigLoader(new JsonMapper(), new ConfigValidator());

        return getSettingsResource().map(resource ->
            loader.loadConfig(resource, SettingsConfiguration.class).get()
        );

    }

}
