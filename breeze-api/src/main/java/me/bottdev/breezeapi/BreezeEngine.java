package me.bottdev.breezeapi;

import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;

import java.nio.file.Path;

public interface BreezeEngine {

    BreezeContext getContext();

    ModuleManager getModuleManager();

    BreezeLogger getLogger();

    JsonMapper getJsonMapper();

    Path getDataFolder();

    void start();

    void restart();

    void stop();

}
