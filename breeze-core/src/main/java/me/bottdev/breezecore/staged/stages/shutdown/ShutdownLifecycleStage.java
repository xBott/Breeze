package me.bottdev.breezecore.staged.stages.shutdown;

import me.bottdev.breezecore.StagedBreezeEngine;
import me.bottdev.breezecore.staged.ProcessStage;

public class ShutdownLifecycleStage implements ProcessStage {

    @Override
    public String getName() {
        return "Shutdown Lifecycle";
    }

    @Override
    public void process(StagedBreezeEngine engine) {
        engine.getLifecycleManager().shutdownAll();
    }

}
