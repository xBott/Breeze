package me.bottdev.breezecore;

import lombok.Getter;
import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.index.BreezeIndexLoader;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleLogger;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;
import me.bottdev.breezecore.di.SimpleBreezeContext;
import me.bottdev.breezecore.modules.SimpleModuleManager;

import java.nio.file.Path;

@Getter
public class SimpleBreezeEngine implements BreezeEngine {

    private final BreezeIndexLoader indexLoader = new BreezeIndexLoader();
    private final BreezeContext context = new SimpleBreezeContext();
    private final ModuleManager moduleManager = new SimpleModuleManager(this);
    private final BreezeLogger logger = new SimpleLogger("SimpleBreezeEngine");
    private final JsonMapper jsonMapper = new JsonMapper();

    private final Path dataFolder;

    public SimpleBreezeEngine(Path dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public void start() {
        logger.info("Starting engine....");
        loadContext();
        startModuleManager();
        logger.info("Successfully started engine.");
    }

    private void loadContext() {
        ClassLoader classLoader = getClass().getClassLoader();
        context.getContextReader().read(classLoader);
    }

    private void startModuleManager() {
        moduleManager.loadAll();
    }

    @Override
    public void restart() {
        logger.info("Restating engine....");
        restartModuleManager();
        logger.info("Successfully restarted engine.");
    }

    private void restartModuleManager() {
        moduleManager.restartAll();
    }

    @Override
    public void stop() {
        logger.info("Stopping engine....");
        stopModuleManager();
        logger.info("Successfully stopped engine.");
    }

    private void stopModuleManager() {
        moduleManager.unloadAll();
    }

}
