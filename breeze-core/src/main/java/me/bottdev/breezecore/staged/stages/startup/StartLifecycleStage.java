package me.bottdev.breezecore.staged.stages.startup;

import me.bottdev.breezecore.StagedBreezeEngine;
import me.bottdev.breezecore.staged.ProcessStage;

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
