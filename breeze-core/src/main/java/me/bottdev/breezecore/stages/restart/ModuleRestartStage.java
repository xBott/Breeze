package me.bottdev.breezecore.stages.restart;

import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezecore.StagedBreezeEngine;

public class ModuleRestartStage implements ProcessStage {

    @Override
    public String getName() {
        return "Module Restart";
    }

    @Override
    public void process(StagedBreezeEngine engine) {
        engine.getContext().get(ModuleManager.class).ifPresent(ModuleManager::restartAll);
    }

}
