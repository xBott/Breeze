package me.bottdev.breezepaper;

import lombok.Getter;
import me.bottdev.breezecore.StagedBreezeEngine;
import me.bottdev.breezecore.staged.StagePriority;
import me.bottdev.breezepaper.stages.PaperCommandLoaderStage;
import me.bottdev.breezepaper.stages.PaperModuleLoaderStage;
import me.bottdev.breezepaper.stages.PaperSupplierRegistrationStage;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class BreezePaper extends JavaPlugin {

    @Getter
    private static BreezePaper instance;
    private StagedBreezeEngine engine;

    @Override
    public void onEnable() {
        instance = this;

        engine = new StagedBreezeEngine(getDataPath().toAbsolutePath());
        engine.getStartupProcess()
                .addStage(
                        new PaperSupplierRegistrationStage(this),
                        StagePriority.HIGHEST
                )
                .addStage(
                        new PaperModuleLoaderStage(getClassLoader(), getDataFolder().toPath().resolve("modules")),
                        StagePriority.HIGH
                )
                .addStage(
                        new PaperCommandLoaderStage(),
                        StagePriority.HIGH
                );

        engine.start();
    }

    @Override
    public void onDisable() {
        engine.shutdown();
    }

}
