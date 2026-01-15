package me.bottdev.breezeadmin.listeners;

import me.bottdev.breezeadmin.config.AdminConfigLoader;
import me.bottdev.breezeadmin.translation.AdminTranslationLoader;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.events.Listener;
import me.bottdev.breezeapi.events.annotations.Listen;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.platforms.SLF4JLogPlatform;
import me.bottdev.breezeapi.resource.watcher.events.ResourceModifyEvent;
import me.bottdev.breezeapi.resource.watcher.ResourceWatchSubject;

@Component
public class ResourceListener implements Listener {

    private final BreezeLogger logger = SLF4JLogPlatform.getFactory().simple("ResourceListener");
    private final AdminConfigLoader adminConfigLoader;
    private final AdminTranslationLoader adminTranslationLoader;

    @Inject
    public ResourceListener(
            AdminConfigLoader adminConfigLoader,
            AdminTranslationLoader adminTranslationLoader
    ) {
        this.adminConfigLoader = adminConfigLoader;
        this.adminTranslationLoader = adminTranslationLoader;
    }

    @Listen(priority = 100)
    public void onResourceChanged(ResourceModifyEvent event) {

        ResourceWatchSubject<?> subject = event.getWatchSubject();
        String eventId = subject.getEventId();

        switch (eventId) {
            case "admin_settings_reload":
                adminConfigLoader.getSettings().ifPresent(configuration ->
                        logger.info("Configuration is updated. New version is {}", configuration.getVersion())
                );
                break;
            case "admin_translations_reload":
                adminTranslationLoader.getTranslationModule();
                logger.info("Translation module is updated.");
                break;
        }

    }

}
