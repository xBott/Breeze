package me.bottdev.breezeadmin.resource;

import me.bottdev.breezeapi.cache.proxy.Cacheable;
import me.bottdev.breezeapi.cache.proxy.annotations.CachePut;
import me.bottdev.breezeapi.di.annotations.Proxy;
import me.bottdev.breezeapi.resource.ResourceTree;
import me.bottdev.breezeapi.resource.annotations.HotReload;
import me.bottdev.breezeapi.resource.annotations.sources.JarSource;
import me.bottdev.breezeapi.resource.proxy.ResourceProvider;
import me.bottdev.breezeapi.resource.annotations.sources.DriveSource;
import me.bottdev.breezeapi.resource.annotations.ProvideResource;
import me.bottdev.breezeapi.resource.types.file.SingleFileResource;

import java.util.Optional;

@Proxy
public interface AdminResourceProvider extends ResourceProvider, Cacheable {

    @CachePut(group = "admin_settings", key = "resource", size = 2, ttl = 60_000)
    @ProvideResource
    @DriveSource(path = "modules/Admin/settings.json", defaultValue = "{}")
    @HotReload(eventId = "admin_settings_reload")
    Optional<SingleFileResource> getSettingsResource();


    @CachePut(group = "admin_settings", key = "translation_tree", size = 2, ttl = 60_000)
    @ProvideResource(isTree = true)
    @DriveSource(path = "modules/Admin/translations")
    @JarSource(path = "translations")
    @HotReload(eventId = "admin_translations_reload") //Добавить поддержку деревьев
    ResourceTree<SingleFileResource> getTranslationTree();

}
