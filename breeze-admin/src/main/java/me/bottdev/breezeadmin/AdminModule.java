package me.bottdev.breezeadmin;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeadmin.config.AdminsConfiguration;
import me.bottdev.breezeapi.config.ConfigLoader;
import me.bottdev.breezeapi.config.validation.ConfigValidator;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.modules.Module;
import me.bottdev.breezeapi.modules.ModuleStatus;
import me.bottdev.breezeapi.modules.annotations.ModuleInfo;
import me.bottdev.breezeapi.log.SimpleLogger;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;

import java.io.File;

@ModuleInfo(name = "Admin", version = "0.0.1")
@RequiredArgsConstructor
public class AdminModule implements Module {

    private ModuleStatus moduleStatus = ModuleStatus.DISABLED;
    private final BreezeLogger logger = new SimpleLogger("BreezeAdmin");

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
        ConfigLoader jsonConfigLoader = new ConfigLoader(new JsonMapper(), new ConfigValidator());
        File file = new File("/Users/romanplakhotniuk/java/Breeze/modules/admin/conf.json");
        jsonConfigLoader.loadConfig(file, AdminsConfiguration.class).ifPresent(config -> {
            logger.info("Max admin count is {}", config.getMaxCount());
        });
    }

    @Override
    public void onRestart() {
    }

    @Override
    public void onDisable() {

    }
}
