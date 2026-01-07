package me.bottdev.breezecore.staged.stages.restart;

import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezecore.StagedBreezeEngine;
import me.bottdev.breezecore.staged.ProcessStage;

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
