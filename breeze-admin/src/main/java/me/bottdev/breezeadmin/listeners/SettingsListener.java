package me.bottdev.breezeadmin.listeners;

import me.bottdev.breezeadmin.providers.SettingsProvider;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.events.Listener;
import me.bottdev.breezeapi.events.annotations.Listen;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezeapi.resource.events.ResourceWatchEvent;
import me.bottdev.breezeapi.resource.watcher.ResourceWatchSubject;

@Component
public class SettingsListener implements Listener {

    private final BreezeLogger logger = new SimpleLogger("SettingsListener");
    private final SettingsProvider settingsProvider;

    @Inject
    public SettingsListener(SettingsProvider settingsProvider) {
        this.settingsProvider = settingsProvider;
    }

    @Listen(priority = 100)
    public void onSettingsChanged(ResourceWatchEvent event) {

        ResourceWatchSubject subject = event.getWatchSubject();
        String eventId = subject.getEventId();

        if (eventId.equalsIgnoreCase("settings_reload")) {
            settingsProvider.loadSettingsConfiguration().ifPresent(configuration ->
                    logger.info("Configuration is updated. New version is {}", configuration.getVersion())
            );
        }

    }

}
