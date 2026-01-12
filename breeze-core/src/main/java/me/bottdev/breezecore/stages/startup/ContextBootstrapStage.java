package me.bottdev.breezecore.stages.startup;

import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.ContextBootstrapper;
import me.bottdev.breezeapi.index.BreezeIndexBucket;
import me.bottdev.breezeapi.index.BreezeIndexLoader;
import me.bottdev.breezecore.StagedBreezeEngine;

public class ContextBootstrapStage implements ProcessStage {

    @Override
    public String getName() {
        return "Context Load";
    }

    @Override
    public void process(StagedBreezeEngine engine) {
        BreezeIndexLoader indexLoader = engine.getIndexLoader();
        ContextBootstrapper contextBootstrapper = engine.getContextBootstrapper();
        BreezeContext context = engine.getContext();

        ClassLoader classLoader = getClass().getClassLoader();
        BreezeIndexBucket bucket = indexLoader.loadFromClassloader(classLoader);
        contextBootstrapper.bootstrap(context, classLoader, bucket);
    }

}
