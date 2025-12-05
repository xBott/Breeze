package me.bottdev.breezeapi;

import me.bottdev.breezeapi.autoload.AutoLoaderRegistry;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.ContextBootstrapper;
import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.index.BreezeIndexLoader;
import me.bottdev.breezeapi.log.TreeLogger;
import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezeapi.serialization.MapperRegistry;

import java.nio.file.Path;

public interface BreezeEngine {

    TreeLogger getLogger();

    BreezeIndexLoader getIndexLoader();

    MapperRegistry getMapperRegistry();

    ContextBootstrapper getContextBootstrapper();

    BreezeContext getContext();

    AutoLoaderRegistry getAutoLoaderRegistry();

    ModuleManager getModuleManager();

    EventBus getEventBus();

    Path getDataFolder();

    void start();

    void restart();

    void stop();

}
