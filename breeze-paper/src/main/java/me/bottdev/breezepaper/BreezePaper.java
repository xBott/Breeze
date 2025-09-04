package me.bottdev.breezepaper;

import lombok.Getter;
import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezecore.modules.loaders.FolderModuleLoader;
import me.bottdev.breezecore.SimpleBreezeEngine;
import me.bottdev.breezepaper.entity.BreezePlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BreezePaper extends JavaPlugin {

    @Getter
    private static BreezePaper instance;
    @Getter
    private BreezeEngine engine;

    @Override
    public void onEnable() {
        instance = this;
        engine = new SimpleBreezeEngine();
        addFolderModuleLoader();
        engine.start();


        getOnlinePlayers().forEach(player -> {
            player.sendMessage("Location of player {} is {}", player.getName(), player.getLocation());
        });

    }

    @Override
    public void onDisable() {
        engine.stop();
    }

    private void addFolderModuleLoader() {
        ModuleManager moduleManager = engine.getModuleManager();
        ClassLoader parentClassLoader = engine.getClass().getClassLoader();
        Path directory = Paths.get("").toAbsolutePath().resolve("modules");
        FolderModuleLoader loader = new FolderModuleLoader(parentClassLoader, directory);
        moduleManager.addModuleLoader(loader);
    }

    public static List<BreezePlayer> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(BreezePlayer::new).collect(Collectors.toList());
    }

    public static BreezePlayer getPlayerByUUID(UUID uuid) {
        return new BreezePlayer(Bukkit.getPlayer(uuid));
    }

    public static BreezePlayer getPlayerByName(String name) {
        return new BreezePlayer(Bukkit.getPlayer(name));
    }

}
