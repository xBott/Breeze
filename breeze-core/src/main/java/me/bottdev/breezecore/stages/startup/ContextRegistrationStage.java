package me.bottdev.breezecore.stages.startup;

import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.autoload.AutoLoaderRegistry;
import me.bottdev.breezeapi.cache.CacheManager;
import me.bottdev.breezeapi.cache.CacheManagerBuilder;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.SupplyType;
import me.bottdev.breezeapi.di.suppliers.SingletonSupplier;
import me.bottdev.breezeapi.i18n.TranslationModuleManager;
import me.bottdev.breezeapi.lifecycle.LifecycleManager;
import me.bottdev.breezeapi.log.BreezeLogPlatform;
import me.bottdev.breezeapi.process.PipelineNodeScope;
import me.bottdev.breezeapi.resource.watcher.ResourceWatcherBuilder;
import me.bottdev.breezeapi.resource.watcher.types.SingleResourceWatcher;
import me.bottdev.breezeapi.resource.watcher.types.TreeResourceWatcher;
import me.bottdev.breezeapi.serialization.MapperRegistry;
import me.bottdev.breezeapi.serialization.MapperType;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;
import me.bottdev.breezecore.StagedBreezeEngine;
import me.bottdev.breezecore.events.SimpleEventBus;
import me.bottdev.breezecore.lifecycle.SimpleLifecycleManager;
import me.bottdev.breezecore.modules.SimpleModuleManager;
import me.bottdev.breezecore.stages.EngineProcessStage;

import java.util.function.Supplier;

public class ContextRegistrationStage implements EngineProcessStage {

    @Override
    public void apply(PipelineNodeScope scope) {

        BreezeEngine engine = getEngine(scope);

        registerSuppliers(engine, scope);
        registerComponents(engine, scope);

    }

    private void registerSuppliers(BreezeEngine engine, PipelineNodeScope scope) {

        registerSupplier(engine, scope, "breezeEngine", () -> engine);
        registerSupplier(engine, scope, "loggerFactory", engine::getLoggerFactory);
        registerSupplier(engine, scope, "mainLogger", engine::getLogger);
        registerSupplier(engine, scope, "mapperRegistry", () -> {
            MapperRegistry mapperRegistry = new MapperRegistry();
            mapperRegistry.registerMapper(new MapperType(JsonMapper.class, "json"), new JsonMapper());
            return mapperRegistry;
        });

    }

    private void registerComponents(BreezeEngine engine, PipelineNodeScope scope) {

        registerComponent(engine, scope, "lifecycleManager", SimpleLifecycleManager.class);
        registerComponent(engine, scope, "autoLoaderRegistry", AutoLoaderRegistry.class);
        registerComponent(engine, scope, "moduleManager", SimpleModuleManager.class);
        registerComponent(engine, scope, "eventBus", SimpleEventBus.class);

    }

    private <T> void registerSupplier(BreezeEngine engine, PipelineNodeScope scope, String name, Supplier<T> supplier) {

        BreezeContext context = engine.getContext();

        T value = supplier.get();

        context.addObjectSupplier(name, new SingletonSupplier(value));

        scope.getTrace().info("Successfully registered supplier \"{}\".", name);

    }

    private <T> void registerComponent(BreezeEngine engine, PipelineNodeScope scope, String name, Class<T> clazz) {

        BreezeContext context = engine.getContext();

        if (context.createComponent(name, SupplyType.SINGLETON, clazz)) {
            scope.getTrace().info("Successfully registered component \"{}\".", name);

        } else {
            scope.error("Failed to register context component \"" + name + "\".", true);

        }

    }

    @Override
    public void process(StagedBreezeEngine engine) {
        BreezeContext context = engine.getContext();
        BreezeLogPlatform logPlatform = engine.getLogPlatform();
        LifecycleManager lifecycleManager = engine.getLifecycleManager();

        registerEventBus(context, lifecycleManager);
        registerTranslationModuleManager(context);

        engine.getLogger().info("Successfully added suppliers to context.");
    }

    private void registerMapperRegistry(BreezeContext context) {

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
