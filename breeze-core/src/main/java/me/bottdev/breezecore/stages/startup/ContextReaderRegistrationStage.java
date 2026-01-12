package me.bottdev.breezecore.stages.startup;

import me.bottdev.breezeapi.cache.CacheManager;
import me.bottdev.breezeapi.cache.proxy.CacheProxyHandlerFactory;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.ContextBootstrapper;
import me.bottdev.breezeapi.di.proxy.ProxyFactoryRegistry;
import me.bottdev.breezeapi.resource.proxy.ResourceProxyHandlerFactory;
import me.bottdev.breezeapi.resource.source.ResourceSourceRegistry;
import me.bottdev.breezeapi.resource.source.SourceType;
import me.bottdev.breezeapi.resource.source.types.DriveResourceSource;
import me.bottdev.breezeapi.resource.source.types.DummyResourceSource;
import me.bottdev.breezeapi.resource.source.types.JarResourceSource;
import me.bottdev.breezeapi.resource.watcher.types.SingleResourceWatcher;
import me.bottdev.breezeapi.resource.watcher.types.TreeResourceWatcher;
import me.bottdev.breezecore.StagedBreezeEngine;
import me.bottdev.breezecore.di.readers.ComponentReader;
import me.bottdev.breezecore.di.readers.ProxyReader;
import me.bottdev.breezecore.di.readers.SupplierReader;
import me.bottdev.breezecore.di.resolver.ComponentDependencyResolver;

import java.nio.file.Path;

public class ContextReaderRegistrationStage implements ProcessStage {

    @Override
    public String getName() {
        return "Context Bootstrapper Readers Registration";
    }

    @Override
    public void process(StagedBreezeEngine engine) {

        BreezeContext context = engine.getContext();
        Path dataFolder = engine.getDataFolder();
        ContextBootstrapper contextBootstrapper = engine.getContextBootstrapper();
        TreeLogger logger = engine.getLogger();

        CacheManager cacheManager = context.get(CacheManager.class).orElse(null);
        SingleResourceWatcher singleResourceWatcher = context.get(SingleResourceWatcher.class).orElse(null);
        TreeResourceWatcher treeResourceWatcher = context.get(TreeResourceWatcher.class).orElse(null);

        if (cacheManager == null || singleResourceWatcher == null || treeResourceWatcher == null) return;

        ResourceSourceRegistry resourceSourceRegistry = new ResourceSourceRegistry()
                .register(SourceType.DRIVE, new DriveResourceSource(dataFolder))
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

}
