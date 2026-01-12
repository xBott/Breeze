package me.bottdev.breezeapi;

import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.log.BreezeLogPlatform;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.BreezeLoggerFactory;
import me.bottdev.breezeapi.process.PipelineExecutor;

import java.nio.file.Path;

public interface BreezeEngine {

    BreezeLogPlatform getLogPlatform();

    BreezeLoggerFactory getLoggerFactory();
    BreezeLogger getLogger();

    PipelineExecutor getPipelineExecutor();

//    SimpleLifecycleManager getLifecycleManager();
//
//    BreezeIndexLoader getIndexLoader();
//
//    ContextBootstrapper getContextBootstrapper();

    BreezeContext getContext();

//    AutoLoaderRegistry getAutoLoaderRegistry();

    Path getDataFolder();

    void start();

    void restart();

    void shutdown();

}
