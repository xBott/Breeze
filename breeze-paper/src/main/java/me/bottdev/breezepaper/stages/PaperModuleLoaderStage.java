package me.bottdev.breezepaper.stages;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.process.PipelineNodeScope;
import me.bottdev.breezecore.stages.EngineProcessStage;

import java.nio.file.Path;

@RequiredArgsConstructor
public class PaperModuleLoaderStage implements EngineProcessStage {

    private final ClassLoader parentClassLoader;
    private final Path directory;

//    @Override
//    public String getName() {
//        return "Paper module loader";
//    }
//
//    @Override
//    public void process(StagedBreezeEngine engine) {
//        engine.getContext().get(ModuleManager.class).ifPresent(moduleManager ->
//                engine.getContext().get(TreeLogger.class, "mainLogger").ifPresent(treeLogger -> {
//
//                    DependencyModuleLoader loader =
//                            new DependencyModuleLoader(engine.getLogger(), parentClassLoader, engine, directory);
//                    moduleManager.addModuleLoader(loader);
//
//                })
//        );
//    }

    @Override
    public void apply(PipelineNodeScope scope) {

    }

}
