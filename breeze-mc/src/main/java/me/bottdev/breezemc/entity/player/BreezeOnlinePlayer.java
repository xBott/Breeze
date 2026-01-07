package me.bottdev.breezemc.entity.player;

import me.bottdev.breezemc.chat.TranslatableMessageReceiver;
import me.bottdev.breezemc.entity.BreezeLivingEntity;

public interface BreezeOnlinePlayer extends BreezePlayer, BreezeLivingEntity, TranslatableMessageReceiver {

    BreezeOfflinePlayer getOfflinePlayer();

}
