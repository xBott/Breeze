package me.bottdev.breezecore.stages;

import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.process.PipelineContext;
import me.bottdev.breezeapi.process.PipelineContextKey;
import me.bottdev.breezeapi.process.PipelineNodeScope;
import me.bottdev.breezeapi.process.ProcessStage;

import java.util.Optional;

public interface EngineProcessStage extends ProcessStage {

    default BreezeEngine getEngine(PipelineNodeScope scope) {
        PipelineContext context = scope.getContext();
        Optional<BreezeEngine> engineOptional = context.get(PipelineContextKey.of("breezeEngine", BreezeEngine.class));
        if (engineOptional.isEmpty()) {
            scope.error("Could not get Breeze Engine from pipeline context.", true);
            return null;
        }
        return engineOptional.get();
    }

}
