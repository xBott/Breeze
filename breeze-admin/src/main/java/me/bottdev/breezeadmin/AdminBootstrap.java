package me.bottdev.breezeadmin;

import me.bottdev.breezeadmin.providers.SettingsProvider;
import me.bottdev.breezeapi.components.bootstrap.Bootstrap;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.di.annotations.Named;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleTreeLogger;

@Component
public class AdminBootstrap implements Bootstrap {

    private final BreezeLogger logger = new SimpleTreeLogger("AdminBootstrap");
    private final SettingsProvider settingsProvider;

    @Inject
    public AdminBootstrap(@Named("settingsProvider") SettingsProvider settingsProvider) {
        this.settingsProvider = settingsProvider;
    }

    @Override
    public void bootstrap() {
        settingsProvider.getSettingsResource()
                .map(resource -> resource.read().orElse(""))
                .ifPresent(logger::info);

        settingsProvider.getSettingsConfiguration().ifPresent(configuration -> {
            logger.info("Module version is {}", configuration.getVersion());
        });
    }

}
