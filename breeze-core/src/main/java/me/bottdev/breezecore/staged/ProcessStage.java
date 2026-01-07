package me.bottdev.breezecore.staged;

import me.bottdev.breezecore.StagedBreezeEngine;

public interface ProcessStage {

    String getName();

    void process(StagedBreezeEngine engine);

}
