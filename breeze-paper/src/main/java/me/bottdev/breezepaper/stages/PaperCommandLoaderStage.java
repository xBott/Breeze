package me.bottdev.breezepaper.stages;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.process.PipelineNodeScope;
import me.bottdev.breezecore.stages.EngineProcessStage;

@RequiredArgsConstructor
public class PaperCommandLoaderStage implements EngineProcessStage {

    @Override
    public void apply(PipelineNodeScope scope) {
//        getEngine(scope).getContext().injectConstructor(PaperCommandAutoLoader.class).ifPresent(autoLoader ->
//                engine.getAutoLoaderRegistry().register(Command.class, autoLoader)
//        );
    }

}
