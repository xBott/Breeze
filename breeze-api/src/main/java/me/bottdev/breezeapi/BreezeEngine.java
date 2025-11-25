package me.bottdev.breezeapi;

import me.bottdev.breezeapi.autoload.AutoLoaderRegistry;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.index.BreezeIndexLoader;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.TreeLogger;
import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;

import java.nio.file.Path;

public interface BreezeEngine {

    TreeLogger getLogger();

    BreezeIndexLoader getIndexLoader();

    BreezeContext getContext();

    AutoLoaderRegistry getAutoLoaderRegistry();

    ModuleManager getModuleManager();

    EventBus getEventBus();

    JsonMapper getJsonMapper();

    Path getDataFolder();

    void start();

    void restart();

    void stop();

}
