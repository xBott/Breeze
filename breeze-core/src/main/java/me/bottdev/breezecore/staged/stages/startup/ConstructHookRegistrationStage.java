package me.bottdev.breezecore.staged.stages.startup;

import me.bottdev.breezeapi.autoload.AutoLoaderRegistry;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezecore.StagedBreezeEngine;
import me.bottdev.breezecore.staged.ProcessStage;

public class ConstructHookRegistrationStage implements ProcessStage {

    @Override
    public String getName() {
        return "Construct Hook Registration";
    }

    @Override
    public void process(StagedBreezeEngine engine) {
        BreezeContext context = engine.getContext();
        AutoLoaderRegistry autoLoaderRegistry = engine.getAutoLoaderRegistry();
        registerAutoLoaderHook(context, autoLoaderRegistry);
        engine.getLogger().info("Successfully registered autoload construct hook.");
    }

    private void registerAutoLoaderHook(BreezeContext context, AutoLoaderRegistry autoLoaderRegistry) {
        context.registerConstructHook(autoLoaderRegistry::accept);
    }

}
