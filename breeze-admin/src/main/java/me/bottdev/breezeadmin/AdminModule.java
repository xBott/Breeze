package me.bottdev.breezeadmin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeadmin.config.AdminConfigLoader;
import me.bottdev.breezeadmin.translation.AdminTranslationLoader;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.i18n.TranslationModule;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezeapi.modules.Module;
import me.bottdev.breezeapi.modules.ModuleStatus;
import me.bottdev.breezeapi.modules.annotations.ModuleInfo;

import java.io.File;

@ModuleInfo(name = "Admin", version = "0.0.1")
@RequiredArgsConstructor
public class AdminModule extends Module {

    @Getter
    private final BreezeLogger logger = new SimpleLogger("BreezeAdmin");

    private final File dataFolder;
    private ModuleStatus moduleStatus = ModuleStatus.DISABLED;

    @Inject
    private AdminConfigLoader adminConfigLoader;
    @Inject
    private AdminTranslationLoader adminTranslationLoader;

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
        adminConfigLoader.getSettings().ifPresent(configuration -> {
            logger.info("Module version is {}", configuration.getVersion());
        });

        TranslationModule translationModule = adminTranslationLoader.getTranslationModule();
        String moduleName = translationModule.getName();
        logger.info("Loaded translation module \"{}\"", moduleName);
    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onDisable() {

    }

}
