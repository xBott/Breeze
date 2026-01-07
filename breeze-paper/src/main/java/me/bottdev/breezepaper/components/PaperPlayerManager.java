package me.bottdev.breezepaper.components;

import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.i18n.I18n;
import me.bottdev.breezeapi.i18n.TranslationModuleManager;
import me.bottdev.breezepaper.entity.player.PaperOfflinePlayer;
import me.bottdev.breezepaper.entity.player.PaperOnlinePlayer;
import me.bottdev.breezepaper.text.BreezeAdventureText;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class PaperPlayerManager {

    private final TranslationModuleManager translationModuleManager;
    private final BreezeAdventureText adventureText;

    @Inject
    public PaperPlayerManager(
            TranslationModuleManager translationModuleManager,
            BreezeAdventureText adventureText
    ) {
        this.translationModuleManager = translationModuleManager;
        this.adventureText = adventureText;
    }

    private Locale getPlayerLocale(Player player) {
        return player.locale();
    }

    public PaperOnlinePlayer getPlayerByBukkit(Player player) {
        Locale locale = getPlayerLocale(player);
        I18n i18n = translationModuleManager.simple(locale);
        return new PaperOnlinePlayer(player, i18n, adventureText);
    }

    public Optional<PaperOnlinePlayer> getPlayerByOffline(PaperOnlinePlayer offlinePlayer) {
        return getPlayerByUUID(offlinePlayer.getUUID());
    }

    public Optional<PaperOnlinePlayer> getPlayerByUUID(UUID uuid) {

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return Optional.empty();
        }

        PaperOnlinePlayer breezePlayer = getPlayerByBukkit(player);
        return Optional.of(breezePlayer);

    }

    public Optional<PaperOnlinePlayer> getPlayerByName(String name) {

        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            return Optional.empty();
        }

        PaperOnlinePlayer breezePlayer = getPlayerByBukkit(player);
        return Optional.of(breezePlayer);

    }

    public List<PaperOnlinePlayer> getPlayers() {
        return Bukkit.getOnlinePlayers().stream().map(this::getPlayerByBukkit).collect(Collectors.toList());
    }


    public PaperOfflinePlayer getOfflinePlayerByBukkit(OfflinePlayer offlinePlayer) {
        return new PaperOfflinePlayer(offlinePlayer);
    }

    public Optional<PaperOfflinePlayer> getOfflinePlayerByUUID(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        PaperOfflinePlayer breezePlayer = getOfflinePlayerByBukkit(offlinePlayer);
        return Optional.of(breezePlayer);
    }

    public Optional<PaperOfflinePlayer> getOfflinePlayerByName(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        PaperOfflinePlayer breezePlayer = getOfflinePlayerByBukkit(offlinePlayer);
        return Optional.of(breezePlayer);
    }

    public List<PaperOfflinePlayer> getOfflinePlayers() {
        return Arrays.stream(Bukkit.getOfflinePlayers()).map(this::getOfflinePlayerByBukkit).toList();
    }

}
