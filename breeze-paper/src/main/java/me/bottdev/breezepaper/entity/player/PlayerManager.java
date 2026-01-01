package me.bottdev.breezepaper.entity.player;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.i18n.I18n;
import me.bottdev.breezeapi.i18n.TranslationModuleManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PlayerManager {

    private final TranslationModuleManager translationModuleManager;

    private Locale getPlayerLocale(Player player) {
        return player.locale();
    }

    public BreezeOnlinePlayer getPlayerByBukkit(Player player) {
        Locale locale = getPlayerLocale(player);
        I18n i18n = translationModuleManager.simple(locale);
        return new BreezeOnlinePlayer(player, i18n);
    }

    public Optional<BreezeOnlinePlayer> getPlayerByOffline(BreezeOnlinePlayer offlinePlayer) {
        return getPlayerByUUID(offlinePlayer.getUUID());
    }

    public Optional<BreezeOnlinePlayer> getPlayerByUUID(UUID uuid) {

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return Optional.empty();
        }

        BreezeOnlinePlayer breezePlayer = getPlayerByBukkit(player);
        return Optional.of(breezePlayer);

    }

    public Optional<BreezeOnlinePlayer> getPlayerByName(String name) {

        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            return Optional.empty();
        }

        BreezeOnlinePlayer breezePlayer = getPlayerByBukkit(player);
        return Optional.of(breezePlayer);

    }

    public List<BreezeOnlinePlayer> getPlayers() {
        return Bukkit.getOnlinePlayers().stream().map(this::getPlayerByBukkit).collect(Collectors.toList());
    }


    public BreezeOfflinePlayer getOfflinePlayerByBukkit(OfflinePlayer offlinePlayer) {
        return new BreezeOfflinePlayer(offlinePlayer);
    }

    public Optional<BreezeOfflinePlayer> getOfflinePlayerByUUID(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        BreezeOfflinePlayer breezePlayer = getOfflinePlayerByBukkit(offlinePlayer);
        return Optional.of(breezePlayer);
    }

    public Optional<BreezeOfflinePlayer> getOfflinePlayerByName(String name) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        BreezeOfflinePlayer breezePlayer = getOfflinePlayerByBukkit(offlinePlayer);
        return Optional.of(breezePlayer);
    }

    public List<BreezeOfflinePlayer> getOfflinePlayers() {
        return Arrays.stream(Bukkit.getOfflinePlayers()).map(this::getOfflinePlayerByBukkit).toList();
    }

}
