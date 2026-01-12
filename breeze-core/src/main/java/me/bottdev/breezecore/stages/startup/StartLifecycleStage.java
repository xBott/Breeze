package me.bottdev.breezecore.stages.startup;

import me.bottdev.breezecore.StagedBreezeEngine;

public class StartLifecycleStage implements ProcessStage {

    @Override
    public String getName() {
        return "Start Lifecycles";
    }

    @Override
    public void process(StagedBreezeEngine engine) {
        engine.getLifecycleManager().startAll();
    }

}
