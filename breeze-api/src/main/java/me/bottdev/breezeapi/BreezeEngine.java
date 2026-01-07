package me.bottdev.breezeapi;

import me.bottdev.breezeapi.autoload.AutoLoaderRegistry;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.ContextBootstrapper;
import me.bottdev.breezeapi.index.BreezeIndexLoader;
import me.bottdev.breezeapi.lifecycle.SimpleLifecycleManager;
import me.bottdev.breezeapi.log.TreeLogger;

import java.nio.file.Path;

public interface BreezeEngine {

    TreeLogger getLogger();

    SimpleLifecycleManager getLifecycleManager();

    BreezeIndexLoader getIndexLoader();

    ContextBootstrapper getContextBootstrapper();

    BreezeContext getContext();

    AutoLoaderRegistry getAutoLoaderRegistry();

    Path getDataFolder();

    void start();

    void restart();

    void shutdown();

}
