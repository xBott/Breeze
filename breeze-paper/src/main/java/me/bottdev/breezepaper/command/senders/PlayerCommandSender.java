package me.bottdev.breezepaper.command.senders;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.CommandSender;
import me.bottdev.breezemc.entity.player.BreezeOnlinePlayer;

@Getter
@RequiredArgsConstructor
public class PlayerCommandSender implements CommandSender {

    private final BreezeOnlinePlayer player;

    @Override
    public void send(String message) {
        player.sendMessage(message);
    }

    @Override
    public void send(String message, Object... args) {
        player.sendMessage(message, args);
    }

}
