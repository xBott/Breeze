package me.bottdev.breezeadmin;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeadmin.providers.SettingsProvider;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezeapi.modules.Module;
import me.bottdev.breezeapi.modules.ModuleStatus;
import me.bottdev.breezeapi.modules.annotations.ModuleInfo;
import me.bottdev.breezeapi.log.types.SimpleTreeLogger;

import java.io.File;

@ModuleInfo(name = "Admin", version = "0.0.1")
@RequiredArgsConstructor
public class AdminModule extends Module {

    private final BreezeLogger logger = new SimpleLogger("BreezeAdmin");

    private final File dataFolder;
    private ModuleStatus moduleStatus = ModuleStatus.DISABLED;

    @Inject
    private SettingsProvider settingsProvider;

    @Override
    public ModuleStatus getStatus() {
        return moduleStatus;
    }

    @Override
    public void setStatus(ModuleStatus status) {
        moduleStatus = status;
    }

    @Override
    public void onEnable() {
        settingsProvider.loadSettingsConfiguration().ifPresent(configuration -> {
            logger.info("Module version is {}", configuration.getVersion());
        });
    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onDisable() {

    }

}
