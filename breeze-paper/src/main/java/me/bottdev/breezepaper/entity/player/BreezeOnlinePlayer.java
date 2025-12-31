package me.bottdev.breezepaper.entity.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.i18n.I18n;
import me.bottdev.breezepaper.chat.TranslatableMessageReceiver;
import me.bottdev.breezepaper.dialog.BreezeDialog;
import me.bottdev.breezepaper.entity.BreezeEntity;
import me.bottdev.breezepaper.entity.BreezeLivingEntity;
import me.bottdev.breezepaper.location.BreezeLocation;
import me.bottdev.breezepaper.text.BreezeText;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public class BreezeOnlinePlayer implements
        BreezeEntity,
        BreezeLivingEntity,
        BreezePlayer,
        TranslatableMessageReceiver
{

    @Getter
    private final Player bukkitPlayer;
    @Getter
    private final I18n i18n;

    @Override
    public Entity getBukkitEntity() {
        return bukkitPlayer;
    }

    @Override
    public BreezeLocation getLocation() {
        return BreezeLocation.fromBukkit(bukkitPlayer.getLocation());
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
