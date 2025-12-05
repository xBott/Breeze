package me.bottdev.breezecore;

import lombok.Getter;
import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.autoload.AutoLoaderRegistry;
import me.bottdev.breezeapi.di.ContextBootstrapper;
import me.bottdev.breezeapi.di.proxy.ProxyFactory;
import me.bottdev.breezeapi.di.proxy.ProxyHandlerRegistry;
import me.bottdev.breezeapi.di.suppliers.SingletonSupplier;
import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.events.Listener;
import me.bottdev.breezeapi.events.ListenerAutoLoader;
import me.bottdev.breezeapi.index.BreezeIndexBucket;
import me.bottdev.breezeapi.index.BreezeIndexLoader;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.log.SimpleLogger;
import me.bottdev.breezeapi.log.TreeLogger;
import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezeapi.serialization.MapperRegistry;
import me.bottdev.breezeapi.serialization.MapperType;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;
import me.bottdev.breezecore.di.SimpleBreezeContext;
import me.bottdev.breezecore.di.readers.ComponentReader;
import me.bottdev.breezecore.di.readers.ProxyReader;
import me.bottdev.breezecore.di.readers.SupplierReader;
import me.bottdev.breezecore.di.resolver.ComponentDependencyResolver;
import me.bottdev.breezecore.modules.SimpleModuleManager;

import java.nio.file.Path;

@Getter
public class SimpleBreezeEngine implements BreezeEngine {

    private final TreeLogger logger = new SimpleLogger("SimpleBreezeEngine");
    private final BreezeIndexLoader indexLoader = new BreezeIndexLoader(logger);
    private final MapperRegistry mapperRegistry = new MapperRegistry();
    private final ContextBootstrapper contextBootstrapper = new ContextBootstrapper();
    private final BreezeContext context = new SimpleBreezeContext(logger);
    private final AutoLoaderRegistry autoLoaderRegistry = new AutoLoaderRegistry(logger);
    private final ModuleManager moduleManager = new SimpleModuleManager(this, logger);
    private final EventBus eventBus = new EventBus(logger);

    private final Path dataFolder;

    public SimpleBreezeEngine(Path dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public void start() {
        logger.info("Starting engine....");

        logger.withSection("BreezeEngine Startup", "", () -> {
            registerMappers();
            registerAutoLoaders();
            registerConstructHooks();
            registerContextBootstrapperReaders();
            loadContext();
            addEngineToContext();
            startModuleManager();
        });

        logger.info("Successfully started engine.");
    }

    private void registerMappers() {
        mapperRegistry.registerMapper(new MapperType(JsonMapper.class, "json"), new JsonMapper());
    }

    private void registerAutoLoaders() {
        autoLoaderRegistry.register(Listener.class, new ListenerAutoLoader(eventBus));
        logger.info("Successfully registered loaders in auto loader registry.");
    }

    private void registerConstructHooks() {
        context.registerConstructHook(autoLoaderRegistry::accept);
        logger.info("Successfully registered autoload construct hook.");
    }

    private void registerContextBootstrapperReaders() {
        contextBootstrapper
                .addReader(new SupplierReader(logger))
                .addReader(new ComponentReader(logger, new ComponentDependencyResolver(logger)))
                .addReader(new ProxyReader(logger, new ProxyFactory(
                        new ProxyHandlerRegistry()
                )));
    }

    private void loadContext() {
        ClassLoader classLoader = getClass().getClassLoader();
        BreezeIndexBucket bucket = indexLoader.loadFromClassloader(classLoader);
        contextBootstrapper.bootstrap(context, classLoader, bucket);
    }

    private void addEngineToContext() {
        context.addObjectSupplier("breezeEngine", new SingletonSupplier(this));
        logger.info("Successfully added breezeEngine supplier.");
    }

    private void startModuleManager() {
        logger.withSection("Loading Module System", "", moduleManager::loadAll);
    }

    @Override
    public void restart() {
        logger.info("Restating engine....");
        logger.withSection("Breeze Engine Restart", "", () -> {
            restartModuleManager();
        });
        logger.info("Successfully restarted engine.");
    }

    private void restartModuleManager() {
        moduleManager.restartAll();
    }

    @Override
    public void stop() {
        logger.info("Stopping engine....");
        logger.withSection("Breeze Engine Stop", "", () -> {
            stopModuleManager();
            unregisterListeners();
        });
        logger.info("Successfully stopped engine.");
    }

    private void stopModuleManager() {
        moduleManager.unloadAll();
    }

    private void unregisterListeners() {
        eventBus.unregisterAllListeners();
    }

}
