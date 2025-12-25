package me.bottdev.breezecore;

import lombok.Getter;
import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.autoload.AutoLoaderRegistry;
import me.bottdev.breezeapi.cache.CacheManager;
import me.bottdev.breezeapi.cache.CacheManagerBuilder;
import me.bottdev.breezeapi.cache.proxy.CacheProxyHandlerFactory;
import me.bottdev.breezeapi.commons.file.temp.TempFiles;
import me.bottdev.breezeapi.components.bootstrap.Bootstrap;
import me.bottdev.breezeapi.components.bootstrap.BootstrapAutoLoader;
import me.bottdev.breezeapi.di.ContextBootstrapper;
import me.bottdev.breezeapi.di.proxy.ProxyFactoryRegistry;
import me.bottdev.breezeapi.di.suppliers.SingletonSupplier;
import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.events.Listener;
import me.bottdev.breezeapi.events.ListenerAutoLoader;
import me.bottdev.breezeapi.index.BreezeIndexBucket;
import me.bottdev.breezeapi.index.BreezeIndexLoader;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.lifecycle.LifecycleManager;
import me.bottdev.breezeapi.log.types.SimpleTreeLogger;
import me.bottdev.breezeapi.log.TreeLogger;
import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezeapi.resource.proxy.ResourceProxyHandlerFactory;
import me.bottdev.breezeapi.resource.source.ResourceSourceRegistry;
import me.bottdev.breezeapi.resource.source.SourceType;
import me.bottdev.breezeapi.resource.source.types.DriveResourceSource;
import me.bottdev.breezeapi.resource.source.types.DummyResourceSource;
import me.bottdev.breezeapi.resource.source.types.JarResourceSource;
import me.bottdev.breezeapi.resource.watcher.ResourceWatcher;
import me.bottdev.breezeapi.resource.watcher.ResourceWatcherBuilder;
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

    private final TreeLogger logger = new SimpleTreeLogger("SimpleBreezeEngine");
    private final MapperRegistry mapperRegistry = new MapperRegistry();

    private final BreezeIndexLoader indexLoader = new BreezeIndexLoader(logger);
    private final ContextBootstrapper contextBootstrapper = new ContextBootstrapper();
    private final BreezeContext context = new SimpleBreezeContext(logger);
    private final AutoLoaderRegistry autoLoaderRegistry = new AutoLoaderRegistry(logger);

    private final ModuleManager moduleManager = new SimpleModuleManager(this, logger);

    private final LifecycleManager lifecycleManager = new LifecycleManager(logger);
    private final CacheManager cacheManager = lifecycleManager.create(new CacheManagerBuilder());
    private final ResourceWatcher resourceWatcher = lifecycleManager.create(new ResourceWatcherBuilder());

    private final EventBus eventBus = new EventBus(logger);

    private final Path dataFolder;

    public SimpleBreezeEngine(Path dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public void start() {
        logger.info("Starting engine....");

        logger.withSection("BreezeEngine Startup", "", () -> {
            addShutdownHook();
            registerMappers();
            registerAutoLoaders();
            registerConstructHooks();
            registerContextBootstrapperReaders();
            addSuppliersToContext();
            loadContext();
            startModuleManager();
        });

        logger.info("Successfully started engine.");
    }

    private void registerMappers() {
        mapperRegistry.registerMapper(new MapperType(JsonMapper.class, "json"), new JsonMapper());
    }

    private void registerAutoLoaders() {
        autoLoaderRegistry
                .register(Listener.class, new ListenerAutoLoader(eventBus))
                .register(Bootstrap.class, new BootstrapAutoLoader());
        logger.info("Successfully registered loaders in auto loader registry.");
    }

    private void registerConstructHooks() {
        context.registerConstructHook(autoLoaderRegistry::accept);
        logger.info("Successfully registered autoload construct hook.");
    }

    private void registerContextBootstrapperReaders() {

        ResourceSourceRegistry resourceSourceRegistry = new ResourceSourceRegistry()
                .register(SourceType.DRIVE, new DriveResourceSource(getDataFolder()))
                .register(SourceType.JAR, new JarResourceSource())
                .register(SourceType.DUMMY, new DummyResourceSource());

        ProxyFactoryRegistry proxyFactoryRegistry = new ProxyFactoryRegistry()
                .register(new CacheProxyHandlerFactory(cacheManager), 0)
                .register(
                        new ResourceProxyHandlerFactory(resourceSourceRegistry, resourceWatcher, cacheManager),
                        1
                );

        contextBootstrapper
                .addReader(new SupplierReader(logger), 0)
                .addReader(new ProxyReader(logger, proxyFactoryRegistry), 5)
                .addReader(new ComponentReader(logger, new ComponentDependencyResolver(logger)), 10);
    }

    private void addSuppliersToContext() {
        context.addObjectSupplier("breezeEngine", new SingletonSupplier(this));
        logger.info("Successfully added breezeEngine supplier.");
    }

    private void loadContext() {
        ClassLoader classLoader = getClass().getClassLoader();
        BreezeIndexBucket bucket = indexLoader.loadFromClassloader(classLoader);
        contextBootstrapper.bootstrap(context, classLoader, bucket);
    }

    private void startModuleManager() {
        logger.withSection("Loading Module System", "", moduleManager::loadAll);
    }

    @Override
    public void restart() {
        logger.info("Restating engine....");
        logger.withSection("Breeze Engine Restart", "", this::restartModuleManager);
        logger.info("Successfully restarted engine.");
    }

    private void restartModuleManager() {
        moduleManager.restartAll();
    }

    @Override
    public void stop() {
        logger.info("Stopping engine....");
        logger.withSection("Breeze Engine Stop", "", () -> {
            shutdownLifecycleManager();
            unregisterListeners();
            stopModuleManager();
            deleteTempFiles();
        });
        logger.info("Successfully stopped engine.");
    }

    private void stopModuleManager() {
        moduleManager.unloadAll();
    }

    private void unregisterListeners() {
        eventBus.unregisterAllListeners();
    }

    private void shutdownLifecycleManager() {
        lifecycleManager.shutdownAll();
    }

    private void deleteTempFiles() {
        TempFiles.cleanup();
    }

}
