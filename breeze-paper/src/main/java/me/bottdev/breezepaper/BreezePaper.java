package me.bottdev.breezepaper;

import lombok.Getter;
import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.command.Command;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezecore.modules.loaders.DependencyModuleLoader;
import me.bottdev.breezecore.SimpleBreezeEngine;
import me.bottdev.breezepaper.autoloaders.PaperCommandAutoloader;
import me.bottdev.breezepaper.entity.player.BreezePlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public class BreezePaper extends JavaPlugin {

    @Getter
    private static BreezePaper instance;
    @Getter
    private BreezeEngine engine;

    @Inject
    private BreezePlayerManager playerManager;

    @Override
    public void onEnable() {
        instance = this;
        engine = new SimpleBreezeEngine(getDataPath().toAbsolutePath());

        addDependencyModuleLoader();
        addCommandAutoloader();

        engine.start();
        //engine.getContext().createComponent("breezeCommand", SupplyType.SINGLETON, new BreezeCommand());

        playerManager.getPlayers().forEach(player ->
                player.sendMessage("Location of player {} is {}", player.getName(), player.getLocation())
        );

    }

    @Override
    public void onDisable() {
        engine.stop();
    }

    private void addDependencyModuleLoader() {
        ModuleManager moduleManager = engine.getModuleManager();
        ClassLoader parentClassLoader = getClassLoader();
        Path directory = getDataFolder().toPath().resolve("modules");
        DependencyModuleLoader loader = new DependencyModuleLoader(engine.getLogger(), parentClassLoader, engine, directory);
        moduleManager.addModuleLoader(loader);
    }

    private void addCommandAutoloader() {
        engine.getAutoLoaderRegistry().register(Command.class, new PaperCommandAutoloader(this));
    }

}
