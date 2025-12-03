package me.bottdev.breezeadmin;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.modules.Module;
import me.bottdev.breezeapi.modules.ModuleStatus;
import me.bottdev.breezeapi.modules.annotations.ModuleInfo;
import me.bottdev.breezeapi.log.SimpleLogger;

import java.io.File;

@ModuleInfo(name = "Admin", version = "0.0.1")
@RequiredArgsConstructor
public class AdminModule extends Module {

    private final File dataFolder;
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
        logger.info("\n\n\nHELLOO VIETNAM!\n\n\n");
    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onDisable() {

    }

}
