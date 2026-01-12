package me.bottdev.breezepaper.stages;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.Command;
import me.bottdev.breezecore.StagedBreezeEngine;
import me.bottdev.breezepaper.autoloaders.PaperCommandAutoLoader;

@RequiredArgsConstructor
public class PaperCommandLoaderStage implements ProcessStage {

    @Override
    public String getName() {
        return "Paper command loader";
    }

    @Override
    public void process(StagedBreezeEngine engine) {
        engine.getContext().injectConstructor(PaperCommandAutoLoader.class).ifPresent(autoLoader ->
                engine.getAutoLoaderRegistry().register(Command.class, autoLoader)
        );
    }

}
