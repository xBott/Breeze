package me.bottdev.breezepaper;

import lombok.Getter;
import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezecore.modules.loaders.FolderModuleLoader;
import me.bottdev.breezecore.SimpleBreezeEngine;
import me.bottdev.breezepaper.entity.BreezePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
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
        engine = new SimpleBreezeEngine(getDataPath());
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
        ClassLoader parentClassLoader = getClassLoader();
        Path directory = getDataFolder().toPath().resolve("modules");
        FolderModuleLoader loader = new FolderModuleLoader(parentClassLoader, engine.getContext(), directory);
        moduleManager.addModuleLoader(loader);
    }

    public static List<BreezePlayer> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(BreezePlayer::new).collect(Collectors.toList());
    }

    public static Optional<BreezePlayer> getPlayerByUUID(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return Optional.empty();
        }
        return Optional.of(new BreezePlayer(player));
    }

    public static Optional<BreezePlayer> getPlayerByName(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            return Optional.empty();
        }
        return Optional.of(new BreezePlayer(player));
    }

}
