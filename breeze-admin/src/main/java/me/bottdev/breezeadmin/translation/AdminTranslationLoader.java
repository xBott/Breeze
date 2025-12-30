package me.bottdev.breezeadmin.translation;

import me.bottdev.breezeadmin.resource.AdminResourceProvider;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.i18n.TranslationLoader;
import me.bottdev.breezeapi.i18n.TranslationModule;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;

@Component
public class AdminTranslationLoader {

    private final AdminResourceProvider adminResourceProvider;
    private final TranslationLoader translationLoader;

    @Inject
    public AdminTranslationLoader(AdminResourceProvider adminResourceProvider) {
        this.adminResourceProvider = adminResourceProvider;
        this.translationLoader = new TranslationLoader(new JsonMapper());
    }

    public TranslationModule getTranslationModule() {
        return translationLoader.loadModule("Admin", adminResourceProvider.getTranslationTree());
    }

}
