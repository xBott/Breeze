package me.bottdev.breezepaper.entity.player;

import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class BreezeOfflinePlayer implements BreezePlayer {

    @Getter
    private final OfflinePlayer player;

    public BreezeOfflinePlayer(OfflinePlayer player) {
        this.player = player;
    }

    @Override
    public UUID getUUID() {
        return player.getUniqueId();
    }

    @Override
    public String getName() {
        return player.getName();
    }

    public Optional<BreezeOnlinePlayer> getOnlinePlayer() {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer == null) return Optional.empty();
        return Optional.of(new BreezeOnlinePlayer(onlinePlayer));
    }

}
