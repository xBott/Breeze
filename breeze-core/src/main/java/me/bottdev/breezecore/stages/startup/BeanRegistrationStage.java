package me.bottdev.breezecore.stages.startup;

import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.cache.CacheManager;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.BreezeLoggerFactory;
import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezeapi.process.PipelineNodeScope;
import me.bottdev.breezeapi.serialization.MapperRegistry;
import me.bottdev.breezeapi.serialization.MapperType;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;
import me.bottdev.breezecore.events.SimpleEventBus;
import me.bottdev.breezecore.modules.SimpleModuleManager;
import me.bottdev.breezecore.stages.EngineProcessStage;

public class BeanRegistrationStage implements EngineProcessStage {

    @Override
    public void apply(PipelineNodeScope scope) {
        BreezeEngine engine = getEngine(scope);
        registerBeans(engine, scope);
    }

    private void registerBeans(BreezeEngine engine, PipelineNodeScope scope) {

        BreezeContext context = engine.getContext();

        context.bind(BreezeEngine.class)
                .failure(() -> scope.error("Failed to bind \"BreezeEngine\" bean.", true))
                .instance(engine);

        context.bind(BreezeLoggerFactory.class)
                .failure(() -> scope.error("Failed to bind \"BreezeLoggerFactory\" bean.", true))
                .instance(engine.getLoggerFactory());

        context.bind(BreezeLogger.class)
                .qualified("mainLogger")
                .failure(() -> scope.error("Failed to bind \"BreezeLogger\" bean.", true))
                .instance(engine.getLogger());

        context.bind(MapperRegistry.class)
                .failure(() -> scope.error("Failed to bind \"MapperRegistry\" bean.", true))
                .instance(() -> {
                    MapperRegistry mapperRegistry = new MapperRegistry();
                    mapperRegistry.registerMapper(new MapperType(JsonMapper.class, "json"), new JsonMapper());
                    return mapperRegistry;
                });

        context.bind(ModuleManager.class)
                .failure(() -> scope.error("Failed to bind \"ModuleManager\" bean.", true))
                .to(SimpleModuleManager.class);

        context.bind(EventBus.class)
                .failure(() -> scope.error("Failed to bind \"EventBus\" bean.", true))
                .to(SimpleEventBus.class);

        context.bind(CacheManager.class)
                .failure(() -> scope.error("Failed to bind \"CacheManager\" bean.", true))
                .self();

    }

}
