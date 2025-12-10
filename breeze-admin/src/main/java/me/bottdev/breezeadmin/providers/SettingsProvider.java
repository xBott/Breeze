package me.bottdev.breezeadmin.providers;

import me.bottdev.breezeapi.di.annotations.Proxy;
import me.bottdev.breezeapi.resource.ResourceProvider;
import me.bottdev.breezeapi.resource.fallback.Fallback;
import me.bottdev.breezeapi.resource.provide.Source;
import me.bottdev.breezeapi.resource.annotations.Drive;
import me.bottdev.breezeapi.resource.annotations.FallbackMethod;
import me.bottdev.breezeapi.resource.annotations.ProvideResource;
import me.bottdev.breezeapi.resource.types.FileResource;

import java.nio.file.Path;
import java.util.Optional;

@Proxy
public interface SettingsProvider extends ResourceProvider {

    default FileResource getSettingsFallback() {
        return new FileResource(Path.of("settings.json"), new byte[0]);
    }

    @ProvideResource(source = Source.DRIVE, fallback = Fallback.METHOD)
    @Drive(path = "{engine}/Admin/settings.json")
    @FallbackMethod(name = "getSettingsFallback")
    Optional<FileResource> getSettings();

}
