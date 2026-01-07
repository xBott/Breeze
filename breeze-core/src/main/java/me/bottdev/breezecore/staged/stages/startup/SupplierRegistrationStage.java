package me.bottdev.breezecore.staged.stages.startup;

import me.bottdev.breezeapi.cache.CacheManager;
import me.bottdev.breezeapi.cache.CacheManagerBuilder;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.suppliers.SingletonSupplier;
import me.bottdev.breezeapi.i18n.TranslationModuleManager;
import me.bottdev.breezeapi.lifecycle.LifecycleManager;
import me.bottdev.breezeapi.resource.watcher.ResourceWatcherBuilder;
import me.bottdev.breezeapi.resource.watcher.types.SingleResourceWatcher;
import me.bottdev.breezeapi.resource.watcher.types.TreeResourceWatcher;
import me.bottdev.breezeapi.serialization.MapperRegistry;
import me.bottdev.breezeapi.serialization.MapperType;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;
import me.bottdev.breezecore.StagedBreezeEngine;
import me.bottdev.breezecore.events.SimpleEventBus;
import me.bottdev.breezecore.modules.SimpleModuleManager;
import me.bottdev.breezecore.staged.ProcessStage;

public class SupplierRegistrationStage implements ProcessStage {

    @Override
    public String getName() {
        return "Supplier Registration";
    }

    @Override
    public void process(StagedBreezeEngine engine) {
        BreezeContext context = engine.getContext();
        LifecycleManager lifecycleManager = engine.getLifecycleManager();

        context.addObjectSupplier("mainLogger", new SingletonSupplier(engine.getLogger()));
        context.addObjectSupplier("breezeEngine", new SingletonSupplier(engine));

        registerMapperRegistry(context);
        registerModuleManager(context);
        registerEventBus(context, lifecycleManager);
        registerTranslationModuleManager(context);

        engine.getLogger().info("Successfully added suppliers to context.");
    }

    private void registerMapperRegistry(BreezeContext context) {
        MapperRegistry mapperRegistry = new MapperRegistry();
        mapperRegistry.registerMapper(new MapperType(JsonMapper.class, "json"), new JsonMapper());
        context.addObjectSupplier("mapperRegistry", new SingletonSupplier(mapperRegistry));
    }

    private void registerModuleManager(BreezeContext context) {
        context.injectConstructor(SimpleModuleManager.class).ifPresent(moduleManager ->
                context.addObjectSupplier(
                        "moduleManager",
                        new SingletonSupplier(moduleManager)
                )
        );
    }

    private void registerEventBus(BreezeContext context, LifecycleManager lifecycleManager) {
        context.injectConstructor(SimpleEventBus.class).ifPresent(eventBus -> {
            context.addObjectSupplier(
                    "eventBus",
                    new SingletonSupplier(eventBus)
            );

            CacheManager cacheManager = lifecycleManager.create(new CacheManagerBuilder());
            context.addObjectSupplier(
                    "cacheManager",
                    new SingletonSupplier(cacheManager)
            );

            SingleResourceWatcher singleResourceWatcher = lifecycleManager.create(new ResourceWatcherBuilder.Single(eventBus));
            context.addObjectSupplier("singleResourceWatcher",
                    new SingletonSupplier(singleResourceWatcher)
            );

            TreeResourceWatcher treeResourceWatcher = lifecycleManager.create(new ResourceWatcherBuilder.Tree(eventBus));
            context.addObjectSupplier(
                    "treeResourceWatcher",
                    new SingletonSupplier(treeResourceWatcher)
            );

        });
    }

    private void registerTranslationModuleManager(BreezeContext context) {
        context.injectConstructor(TranslationModuleManager.class).ifPresent(translationModuleManager ->
                context.addObjectSupplier(
                        "translationModuleManager",
                        new SingletonSupplier(translationModuleManager)
                )
        );
    }

}
