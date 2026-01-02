package me.bottdev.breezecore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
import me.bottdev.breezeapi.i18n.TranslationModuleManager;
import me.bottdev.breezeapi.index.BreezeIndexBucket;
import me.bottdev.breezeapi.index.BreezeIndexLoader;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.lifecycle.SimpleLifecycleManager;
import me.bottdev.breezeapi.log.types.SimpleTreeLogger;
import me.bottdev.breezeapi.log.TreeLogger;
import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezeapi.resource.proxy.ResourceProxyHandlerFactory;
import me.bottdev.breezeapi.resource.source.ResourceSourceRegistry;
import me.bottdev.breezeapi.resource.source.SourceType;
import me.bottdev.breezeapi.resource.source.types.DriveResourceSource;
import me.bottdev.breezeapi.resource.source.types.DummyResourceSource;
import me.bottdev.breezeapi.resource.source.types.JarResourceSource;
import me.bottdev.breezeapi.resource.watcher.types.SingleResourceWatcher;
import me.bottdev.breezeapi.resource.watcher.ResourceWatcherBuilder;
import me.bottdev.breezeapi.resource.watcher.types.TreeResourceWatcher;
import me.bottdev.breezeapi.serialization.MapperRegistry;
import me.bottdev.breezeapi.serialization.MapperType;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;
import me.bottdev.breezecore.di.LifecycleBreezeContext;
import me.bottdev.breezecore.di.readers.ComponentReader;
import me.bottdev.breezecore.di.readers.ProxyReader;
import me.bottdev.breezecore.di.readers.SupplierReader;
import me.bottdev.breezecore.di.resolver.ComponentDependencyResolver;
import me.bottdev.breezecore.events.SimpleEventBus;
import me.bottdev.breezecore.modules.SimpleModuleManager;

import java.nio.file.Path;

@Getter
@RequiredArgsConstructor
public class SimpleBreezeEngine implements BreezeEngine {

    private final TreeLogger logger = new SimpleTreeLogger("SimpleBreezeEngine");
    private final SimpleLifecycleManager lifecycleManager = new SimpleLifecycleManager(logger);
    private final BreezeIndexLoader indexLoader = new BreezeIndexLoader(logger);
    private final ContextBootstrapper contextBootstrapper = new ContextBootstrapper();
    private final BreezeContext context = new LifecycleBreezeContext(logger, lifecycleManager);
    private final AutoLoaderRegistry autoLoaderRegistry = new AutoLoaderRegistry(logger);

    private final Path dataFolder;
    private final Runnable setupRunnable;

    @Override
    public void start() {
        logger.info("Starting engine....");

        logger.withSection("BreezeEngine Startup", "", () -> {
            addShutdownHook();
            registerSuppliers();
            setup();
            startAllLifecycles();
        });

        logger.info("Successfully started engine.");
    }

    private void registerSuppliers() {

        context.addObjectSupplier("mainLogger", new SingletonSupplier(logger));
        context.addObjectSupplier("breezeEngine", new SingletonSupplier(this));

        MapperRegistry mapperRegistry = new MapperRegistry();
        context.addObjectSupplier("mapperRegistry", new SingletonSupplier(mapperRegistry));

        context.injectConstructor(SimpleModuleManager.class).ifPresent(moduleManager ->
                context.addObjectSupplier(
                        "moduleManager",
                        new SingletonSupplier(moduleManager)
                )
        );

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

        context.injectConstructor(TranslationModuleManager.class).ifPresent(translationModuleManager ->
                context.addObjectSupplier(
                        "translationModuleManager",
                        new SingletonSupplier(translationModuleManager)
                )
        );

        logger.info("Successfully added suppliers to context.");

    }

    private void setup() {
        registerMappers();
        registerAutoLoaders();
        registerConstructHooks();
        registerContextBootstrapperReaders();
        setupRunnable.run();
        loadContext();
    }

    private void registerMappers() {
        context.get(MapperRegistry.class, "mapperRegistry").ifPresent(mapperRegistry ->
                mapperRegistry.registerMapper(new MapperType(JsonMapper.class, "json"), new JsonMapper())
        );
    }

    private void registerAutoLoaders() {
        context.get(EventBus.class, "eventBus").ifPresent(eventBus -> {
            autoLoaderRegistry
                    .register(Listener.class, new ListenerAutoLoader(eventBus))
                    .register(Bootstrap.class, new BootstrapAutoLoader());
            logger.info("Successfully registered loaders in auto loader registry.");
        });
    }

    private void registerConstructHooks() {
        context.registerConstructHook(autoLoaderRegistry::accept);
        logger.info("Successfully registered autoload construct hook.");
    }

    private void registerContextBootstrapperReaders() {

        CacheManager cacheManager = context.get(CacheManager.class, "cacheManager").orElse(null);
        SingleResourceWatcher singleResourceWatcher = context.get(SingleResourceWatcher.class, "singleResourceWatcher").orElse(null);
        TreeResourceWatcher treeResourceWatcher = context.get(TreeResourceWatcher.class, "treeResourceWatcher").orElse(null);

        if (cacheManager == null || singleResourceWatcher == null || treeResourceWatcher == null) return;

        ResourceSourceRegistry resourceSourceRegistry = new ResourceSourceRegistry()
                .register(SourceType.DRIVE, new DriveResourceSource(getDataFolder()))
                .register(SourceType.JAR, new JarResourceSource())
                .register(SourceType.DUMMY, new DummyResourceSource());

        ProxyFactoryRegistry proxyFactoryRegistry = new ProxyFactoryRegistry()
                .register(new CacheProxyHandlerFactory(cacheManager), 0)
                .register(
                        new ResourceProxyHandlerFactory(resourceSourceRegistry, singleResourceWatcher, treeResourceWatcher),
                        1
                );

        contextBootstrapper
                .addReader(new SupplierReader(logger), 0)
                .addReader(new ProxyReader(logger, proxyFactoryRegistry), 5)
                .addReader(new ComponentReader(logger, new ComponentDependencyResolver(logger)), 10);
    }

    private void loadContext() {
        ClassLoader classLoader = getClass().getClassLoader();
        BreezeIndexBucket bucket = indexLoader.loadFromClassloader(classLoader);
        contextBootstrapper.bootstrap(context, classLoader, bucket);
    }

    private void startAllLifecycles() {
        getLifecycleManager().startAll();
    }

    @Override
    public void restart() {
        logger.info("Restating engine....");
        logger.withSection("Breeze Engine Restart", "", this::restartModuleManager);
        logger.info("Successfully restarted engine.");
    }

    private void restartModuleManager() {
        context.get(ModuleManager.class, "moduleManager").ifPresent(ModuleManager::restartAll);
    }

    @Override
    public void stop() {
        logger.info("Stopping engine....");
        logger.withSection("Breeze Engine Stop", "", () -> {
            shutdownLifecycleManager();
            deleteTempFiles();
        });
        logger.info("Successfully stopped engine.");
    }

    private void shutdownLifecycleManager() {
        lifecycleManager.shutdownAll();
    }

    private void deleteTempFiles() {
        TempFiles.cleanup();
    }

}
