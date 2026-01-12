package me.bottdev.breezepaper.stages;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezecore.StagedBreezeEngine;
import me.bottdev.breezecore.modules.loaders.DependencyModuleLoader;

import java.nio.file.Path;

@RequiredArgsConstructor
public class PaperModuleLoaderStage implements ProcessStage {

    private final ClassLoader parentClassLoader;
    private final Path directory;

    @Override
    public String getName() {
        return "Paper module loader";
    }

    @Override
    public void process(StagedBreezeEngine engine) {
        engine.getContext().get(ModuleManager.class).ifPresent(moduleManager ->
                engine.getContext().get(TreeLogger.class, "mainLogger").ifPresent(treeLogger -> {

                    DependencyModuleLoader loader =
                            new DependencyModuleLoader(engine.getLogger(), parentClassLoader, engine, directory);
                    moduleManager.addModuleLoader(loader);

                })
        );
    }
}
