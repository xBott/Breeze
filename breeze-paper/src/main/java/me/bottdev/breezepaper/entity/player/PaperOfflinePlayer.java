package me.bottdev.breezepaper.entity.player;

import lombok.Getter;
import me.bottdev.breezemc.entity.player.BreezeOfflinePlayer;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class PaperOfflinePlayer implements BreezeOfflinePlayer {

    @Getter
    private final OfflinePlayer offlinePlayer;

    public PaperOfflinePlayer(OfflinePlayer player) {
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

}
