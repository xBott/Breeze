package me.bottdev.breezepaper.entity.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.i18n.I18n;
import me.bottdev.breezemc.entity.player.BreezeOfflinePlayer;
import me.bottdev.breezemc.entity.player.BreezeOnlinePlayer;
import me.bottdev.breezemc.world.BreezeLocation;
import me.bottdev.breezepaper.location.PaperLocation;
import me.bottdev.breezepaper.text.BreezeAdventureComponent;
import me.bottdev.breezepaper.text.BreezeAdventureText;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public class PaperOnlinePlayer implements BreezeOnlinePlayer {

    @Getter
    private final Player bukkitPlayer;
    @Getter
    private final I18n i18n;
    private final BreezeAdventureText adventureText;

    @Override
    public BreezeLocation getLocation() {
        return PaperLocation.fromPaper(bukkitPlayer.getLocation());
    }

    @Override
    public double getHealth() {
        return bukkitPlayer.getHealth();
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
    public void sendMessage(String message) {
        BreezeAdventureComponent breezeComponent = adventureText.deserialize(message);
        Component component = breezeComponent.getComponent();
        bukkitPlayer.sendMessage(component);
    }

    public BreezeOfflinePlayer getOfflinePlayer() {
        return new PaperOfflinePlayer(bukkitPlayer);
    }

}
