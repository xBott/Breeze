package me.bottdev.breezecore.stages.startup;

import me.bottdev.breezeapi.autoload.AutoLoaderRegistry;
import me.bottdev.breezeapi.components.bootstrap.Bootstrap;
import me.bottdev.breezeapi.components.bootstrap.BootstrapAutoLoader;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.events.Listener;
import me.bottdev.breezeapi.events.ListenerAutoLoader;
import me.bottdev.breezecore.StagedBreezeEngine;

public class AutoLoaderRegistrationStage implements ProcessStage {

    @Override
    public String getName() {
        return "Auto Loader Registration";
    }

    @Override
    public void process(StagedBreezeEngine engine) {

        AutoLoaderRegistry autoLoaderRegistry = engine.getAutoLoaderRegistry();
        BreezeContext context = engine.getContext();

        registerBootstrapAutoLoader(autoLoaderRegistry);
        registerEventListenerAutoLoader(context, autoLoaderRegistry);

        engine.getLogger().info("Successfully registered loaders in auto loader registry.");
    }

    private void registerBootstrapAutoLoader(AutoLoaderRegistry autoLoaderRegistry) {
        autoLoaderRegistry.register(Bootstrap.class, new BootstrapAutoLoader());
    }

    private void registerEventListenerAutoLoader(BreezeContext context, AutoLoaderRegistry autoLoaderRegistry) {
        context.get(EventBus.class).ifPresent(eventBus ->
                autoLoaderRegistry.register(Listener.class, new ListenerAutoLoader(eventBus))
        );
    }

}
