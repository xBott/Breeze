package me.bottdev.breezeapi;

import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.ContextBootstrap;
import me.bottdev.breezeapi.index.IndexLoader;
import me.bottdev.breezeapi.lifecycle.LifecycleManager;
import me.bottdev.breezeapi.log.BreezeLogPlatform;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.BreezeLoggerFactory;
import me.bottdev.breezeapi.process.PipelineExecutor;

import java.nio.file.Path;

public interface BreezeEngine {

    Path getDataFolder();

    BreezeLogPlatform getLogPlatform();

    BreezeLoggerFactory getLoggerFactory();
    BreezeLogger getLogger();

    PipelineExecutor getPipelineExecutor();

    IndexLoader getIndexLoader();

    ContextBootstrap getContextBootstrap();

    BreezeContext getContext();

    LifecycleManager getLifecycleManager();

    void start();

    void restart();

    void shutdown();

}
