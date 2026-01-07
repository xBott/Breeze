package me.bottdev.breezepaper.stages;

import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.suppliers.SingletonSupplier;
import me.bottdev.breezecore.StagedBreezeEngine;
import me.bottdev.breezecore.staged.ProcessStage;
import me.bottdev.breezepaper.components.PaperPlayerManager;
import me.bottdev.breezepaper.text.BreezeAdventureText;

public class PaperSupplierRegistrationStage implements ProcessStage {

    @Override
    public String getName() {
        return "Paper supplier registration";
    }

    @Override
    public void process(StagedBreezeEngine engine) {
        BreezeContext context = engine.getContext();
        registerAdventureText(context);
        registerPlayerManager(context);
    }

    private void registerAdventureText(BreezeContext context) {
        context.injectConstructor(BreezeAdventureText.class).ifPresent(adventureText ->
                context.addObjectSupplier("adventureText", new SingletonSupplier(adventureText))
        );
    }

    private void registerPlayerManager(BreezeContext context) {
        context.injectConstructor(PaperPlayerManager.class).ifPresent(paperPlayerManager ->
                context.addObjectSupplier("paperPlayerManager", new SingletonSupplier(paperPlayerManager))
        );
    }

}
