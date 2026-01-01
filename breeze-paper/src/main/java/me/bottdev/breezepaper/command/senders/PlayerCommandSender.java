package me.bottdev.breezepaper.command.senders;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.CommandSender;
import me.bottdev.breezepaper.entity.player.BreezeOnlinePlayer;

@Getter
@RequiredArgsConstructor
public class PlayerCommandSender implements CommandSender {

    private final BreezeOnlinePlayer player;

    @Override
    public void send(String message) {
        player.sendMessage(message);
    }

}
