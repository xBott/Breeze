package me.bottdev.breezepaper;

import lombok.Getter;
import me.bottdev.breezeapi.log.platforms.SLF4JLogPlatform;
import me.bottdev.breezecore.SimpleBreezeEngine;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class BreezePaper extends JavaPlugin {

    @Getter
    private static BreezePaper instance;
    private SimpleBreezeEngine engine;

    @Override
    public void onEnable() {
        instance = this;

        engine = new SimpleBreezeEngine(getDataPath().toAbsolutePath(), new SLF4JLogPlatform());
//        engine.getStartupPipeline()
//                .addStage(
//                        new PaperSupplierRegistrationStage(this),
//                        StagePriority.HIGHEST
//                )
//                .addStage(
//                        new PaperModuleLoaderStage(getClassLoader(), getDataFolder().toPath().resolve("modules")),
//                        StagePriority.HIGH
//                )
//                .addStage(
//                        new PaperCommandLoaderStage(),
//                        StagePriority.HIGH
//                );

        engine.start();
    }

    @Override
    public void onDisable() {
        engine.shutdown();
    }

}
