package me.bottdev.breezepaper.entity.player;

import lombok.Getter;
import me.bottdev.breezepaper.MessageReceiver;
import me.bottdev.breezepaper.dialog.BreezeDialog;
import me.bottdev.breezepaper.entity.BreezeEntity;
import me.bottdev.breezepaper.entity.BreezeLivingEntity;
import me.bottdev.breezepaper.location.BreezeLocation;
import me.bottdev.breezepaper.text.BreezeText;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BreezeOnlinePlayer implements BreezeEntity, BreezeLivingEntity, BreezePlayer, MessageReceiver {

    @Getter
    private final Player bukkitPlayer;
    @Getter
    private final BreezeLocation location;

    public BreezeOnlinePlayer(Player player) {
        this.bukkitPlayer = player;
        this.location = BreezeLocation.fromBukkit(player.getLocation());
    }

    @Override
    public Entity getBukkitEntity() {
        return bukkitPlayer;
    }

    @Override
    public UUID getUUID() {
        return bukkitPlayer.getUniqueId();
    }

    @Override
    public String getName() {
        return bukkitPlayer.getName();
    }

    @Override
    public double getHealth() {
        return bukkitPlayer.getHealth();
    }

    @Override
    public void sendMessage(String message) {
        Component component = BreezeText.format(message);
        bukkitPlayer.sendMessage(component);
    }

    public BreezeOfflinePlayer getOfflinePlayer() {
        return new BreezeOfflinePlayer(bukkitPlayer);
    }

    public void openDialog(BreezeDialog dialog) {
        bukkitPlayer.showDialog(dialog.toPaperDialog());
    }

}
