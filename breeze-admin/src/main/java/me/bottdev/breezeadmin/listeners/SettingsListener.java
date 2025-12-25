package me.bottdev.breezeadmin.listeners;

import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.events.Listener;
import me.bottdev.breezeapi.events.annotations.Listen;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezeapi.resource.events.ResourceWatchEvent;
import me.bottdev.breezeapi.resource.types.FileResource;
import me.bottdev.breezeapi.resource.watcher.ResourceWatchSubject;

@Component
public class SettingsListener implements Listener {

    private final BreezeLogger logger = new SimpleLogger("SettingsListener");

    @Listen(priority = 100)
    public void onSettingsChanged(ResourceWatchEvent event) {

        ResourceWatchSubject subject = event.getWatchSubject();

        FileResource resource = subject.getResource();
        String eventId = subject.getEventId();

        logger.info("Received resource hot-reload event: " + eventId);

        if (eventId.equalsIgnoreCase("settings_reload")) {
            String content = resource.readTrimmed().orElse("Empty!");
            logger.info("Settings reload completed. New content: \n{}", content);
        }

    }

}
