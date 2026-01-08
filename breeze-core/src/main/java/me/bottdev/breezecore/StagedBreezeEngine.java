package me.bottdev.breezecore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.autoload.AutoLoaderRegistry;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.ContextBootstrapper;
import me.bottdev.breezeapi.index.BreezeIndexLoader;
import me.bottdev.breezeapi.lifecycle.SimpleLifecycleManager;
import me.bottdev.breezeapi.log.TreeLogger;
import me.bottdev.breezeapi.log.types.SimpleTreeLogger;
import me.bottdev.breezecore.di.LifecycleBreezeContext;
import me.bottdev.breezecore.staged.EngineProcess;
import me.bottdev.breezecore.staged.StagePriority;
import me.bottdev.breezecore.staged.stages.restart.ModuleRestartStage;
import me.bottdev.breezecore.staged.stages.shutdown.DeleteTempFilesStage;
import me.bottdev.breezecore.staged.stages.shutdown.ShutdownLifecycleStage;
import me.bottdev.breezecore.staged.stages.startup.*;

import java.nio.file.Path;

@Getter
@RequiredArgsConstructor
public class StagedBreezeEngine implements BreezeEngine {

    private final TreeLogger logger = new SimpleTreeLogger("StagedBreezeEngine");
    private final SimpleLifecycleManager lifecycleManager = new SimpleLifecycleManager(logger);
    private final BreezeIndexLoader indexLoader = new BreezeIndexLoader(logger);
    private final ContextBootstrapper contextBootstrapper = new ContextBootstrapper();
    private final BreezeContext context = new LifecycleBreezeContext(logger, lifecycleManager);
    private final AutoLoaderRegistry autoLoaderRegistry = new AutoLoaderRegistry(logger);

    private final EngineProcess startupProcess = new EngineProcess("startup")
            .addStage(new SupplierRegistrationStage(), StagePriority.HIGHEST)
            .addStage(new AutoLoaderRegistrationStage(), StagePriority.HIGH)
            .addStage(new ConstructHookRegistrationStage(), StagePriority.NORMAL)
            .addStage(new ContextReaderRegistrationStage(), StagePriority.NORMAL)
            .addStage(new ContextBootstrapStage(), StagePriority.LOW)
            .addStage(new StartLifecycleStage(), StagePriority.LOWEST);

    private final EngineProcess restartProcess = new EngineProcess("restart")
            .addStage(new ModuleRestartStage(), StagePriority.HIGHEST);

    private final EngineProcess shutdownProcess = new EngineProcess("shutdown")
            .addStage(new ShutdownLifecycleStage(), StagePriority.HIGHEST)
            .addStage(new DeleteTempFilesStage(), StagePriority.HIGH);

    private final Path dataFolder;

    @Override
    public void start() {
        logger.info("Starting engine....");

        addShutdownHook();
        logger.withSection("BreezeEngine Startup", "", () -> {
            startupProcess.process(this);
        });

        logger.info("Successfully started engine.");
    }

    @Override
    public void restart() {
        logger.info("Restating engine....");
        logger.withSection("Breeze Engine Restart", "", () ->
                restartProcess.process(this)
        );
        logger.info("Successfully restarted engine.");
    }

    @Override
    public void shutdown() {
        logger.info("Stopping engine....");
        logger.withSection("Breeze Engine Stop", "", () ->
                shutdownProcess.process(this)
        );
        logger.info("Successfully stopped engine.");
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

}
