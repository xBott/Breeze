package me.bottdev.breezecore;

import lombok.Getter;
import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.di.suppliers.SingletonSupplier;
import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.events.Listener;
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
    private final EventBus eventBus = new EventBus();
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
        addEngineToContext();
        startModuleManager();
        logger.info("Successfully started engine.");
    }

    private void loadContext() {
        ClassLoader classLoader = getClass().getClassLoader();
        context.getContextReader().read(classLoader);
        context.registerConstructHook(object -> {
            if  (object instanceof Listener) {
                logger.info("Registering listener " + object.getClass().getSimpleName());
            }
        });
    }

    private void addEngineToContext() {
        context.addObjectSupplier("breezeEngine", new SingletonSupplier(this));
        logger.info("Successfully added breezeEngine supplier.");
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
        unregisterListeners();
        logger.info("Successfully stopped engine.");
    }

    private void stopModuleManager() {
        moduleManager.unloadAll();
    }

    private void unregisterListeners() {
        eventBus.unregisterAllListeners();
    }

}
