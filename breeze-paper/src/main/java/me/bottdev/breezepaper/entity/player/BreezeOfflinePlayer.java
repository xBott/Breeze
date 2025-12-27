package me.bottdev.breezepaper.entity.player;

import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class BreezeOfflinePlayer implements BreezePlayer {

    @Getter
    private final OfflinePlayer offlinePlayer;

    public BreezeOfflinePlayer(OfflinePlayer player) {
        this.offlinePlayer = player;
    }

    @Override
    public UUID getUUID() {
        return offlinePlayer.getUniqueId();
    }

    @Override
    public String getName() {
        return offlinePlayer.getName();
    }

    public Optional<BreezeOnlinePlayer> getOnlinePlayer() {
        Player onlinePlayer = offlinePlayer.getPlayer();
        if (onlinePlayer == null) return Optional.empty();
        return Optional.of(new BreezeOnlinePlayer(onlinePlayer));
    }

}
