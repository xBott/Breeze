package me.bottdev.breezecore;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.log.BreezeLogPlatform;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.BreezeLoggerFactory;
import me.bottdev.breezeapi.process.*;
import me.bottdev.breezeapi.process.executors.SequentialPipelineExecutor;
import me.bottdev.breezecore.di.SimpleBreezeContext;

import java.nio.file.Path;

@Getter
@RequiredArgsConstructor
public class StagedBreezeEngine implements BreezeEngine {

    private final Path dataFolder;
    private final BreezeLogPlatform logPlatform;
    private final BreezeLoggerFactory loggerFactory;
    private final BreezeLogger logger;
    private final PipelineExecutor pipelineExecutor;
    private final BreezeContext context;

    private final ProcessPipeline startupPipeline = ProcessPipeline.of(
            PipelineNode.of(scope -> {}), //Supplier Registration
            PipelineNode.of(scope -> {}), //AutoLoader Setup
            PipelineNode.of(scope -> {}), //Construct Hook Registration
            PipelineNode.of(scope -> {}), //ContextReader Registration
            PipelineNode.of(scope -> {}), //Context Bootstrap
            PipelineNode.of(scope -> {})  //Start Lifecycles
    );

    public StagedBreezeEngine(Path dataFolder, BreezeLogPlatform logPlatform) {
        this.dataFolder = dataFolder;
        this.logPlatform = logPlatform;
        this.loggerFactory = new BreezeLoggerFactory(logPlatform);
        this.logger = loggerFactory.simple("BreezeEngine");
        this.pipelineExecutor = new SequentialPipelineExecutor(logger);
        this.context = new SimpleBreezeContext(logger);
    }

//    private final SimpleLifecycleManager lifecycleManager = new SimpleLifecycleManager(logger);
//    private final BreezeIndexLoader indexLoader = new BreezeIndexLoader(logger);
//    private final ContextBootstrapper contextBootstrapper = new ContextBootstrapper();
//    private final BreezeContext context = new LifecycleBreezeContext(logger, lifecycleManager);
//    private final AutoLoaderRegistry autoLoaderRegistry = new AutoLoaderRegistry(logger);

    @Override
    public void start() {
//        logger.info("Starting engine....");
//
//        addShutdownHook();
//        logger.withSection("BreezeEngine Startup", "", () -> {
//            startupProcess.process(this);
//        });
//
//        logger.info("Successfully started engine.");

        addShutdownHook();

        PipelineContext pipelineContext = new PipelineContext();
        pipelineContext.put(PipelineContextKey.of("breezeEngine", BreezeEngine.class), this);
        pipelineExecutor.execute(startupPipeline, pipelineContext);
    }

    @Override
    public void restart() {
//        logger.info("Restating engine....");
//        logger.withSection("Breeze Engine Restart", "", () ->
//                restartProcess.process(this)
//        );
//        logger.info("Successfully restarted engine.");
    }

    @Override
    public void shutdown() {
//        logger.info("Stopping engine....");
//        logger.withSection("Breeze Engine Stop", "", () ->
//                shutdownProcess.process(this)
//        );
//        logger.info("Successfully stopped engine.");
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

}
