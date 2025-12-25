package me.bottdev.breezeadmin.components;

import me.bottdev.breezeadmin.providers.SettingsProvider;
import me.bottdev.breezeapi.components.bootstrap.Bootstrap;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;

@Component
public class AdminBootstrap implements Bootstrap {

    private final BreezeLogger logger = new SimpleLogger("AdminBootstrap");
    private final SettingsProvider settingsProvider;

    @Inject
    public AdminBootstrap(SettingsProvider settingsProvider) {
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
