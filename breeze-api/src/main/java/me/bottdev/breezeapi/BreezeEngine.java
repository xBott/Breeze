package me.bottdev.breezeapi;

import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;

public interface BreezeEngine {

    BreezeContext getContext();

    ModuleManager getModuleManager();

    BreezeLogger getLogger();

    JsonMapper getJsonMapper();

    void start();

    void restart();

    void stop();

}
