package me.bottdev.breezepaper;

import lombok.Getter;
import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezecore.modules.loaders.DependencyModuleLoader;
import me.bottdev.breezecore.SimpleBreezeEngine;
import me.bottdev.breezepaper.entity.player.BreezeOfflinePlayer;
import me.bottdev.breezepaper.entity.player.BreezeOnlinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.Arrays;
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
        addDependencyModuleLoader();
        engine.start();


        getOnlinePlayers().forEach(player -> {
            player.sendMessage("Location of player {} is {}", player.getName(), player.getLocation());
        });

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

    public static List<BreezeOnlinePlayer> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(BreezeOnlinePlayer::new).collect(Collectors.toList());
    }

    public static Optional<BreezeOnlinePlayer> getPlayerByUUID(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return Optional.empty();
        }
        return Optional.of(new BreezeOnlinePlayer(player));
    }

    public static Optional<BreezeOnlinePlayer> getPlayerByName(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            return Optional.empty();
        }
        return Optional.of(new BreezeOnlinePlayer(player));
    }

    public static List<BreezeOfflinePlayer> getOfflinePlayers() {
        return Arrays.stream(Bukkit.getOfflinePlayers()).map(BreezeOfflinePlayer::new).toList();
    }

    public static Optional<BreezeOfflinePlayer> getOfflinePlayerByUUID(UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        return Optional.of(new BreezeOfflinePlayer(player));
    }

    public static Optional<BreezeOfflinePlayer> getOfflinePlayerByName(String name) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        return Optional.of(new BreezeOfflinePlayer(player));
    }

}
