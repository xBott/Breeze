package me.bottdev.breezepaper.entity;

import lombok.Getter;
import me.bottdev.breezepaper.MessageReceiver;
import me.bottdev.breezepaper.location.BreezeLocation;
import me.bottdev.breezepaper.text.BreezeText;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BreezePlayer implements BreezeEntity, MessageReceiver {

    @Getter
    private final Player bukkitPlayer;
    @Getter
    private final BreezeLocation location;

    public BreezePlayer(Player player) {
        this.bukkitPlayer = player;
        this.location = BreezeLocation.fromBukkit(player.getLocation());
    }

    @Override
    public Entity getBukkitEntity() {
        return bukkitPlayer;
    }

    public UUID getUUID() {
        return bukkitPlayer.getUniqueId();
    }

    public String getName() {
        return bukkitPlayer.getName();
    }

    public boolean isOnline() {
        return bukkitPlayer.isOnline();
    }

    @Override
    public void sendMessage(String message) {
        Component component = BreezeText.format(message);
        bukkitPlayer.sendMessage(component);
    }
}
