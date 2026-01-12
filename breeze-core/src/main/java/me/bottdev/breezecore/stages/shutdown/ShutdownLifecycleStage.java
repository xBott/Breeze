package me.bottdev.breezecore.stages.shutdown;

import me.bottdev.breezecore.StagedBreezeEngine;

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
