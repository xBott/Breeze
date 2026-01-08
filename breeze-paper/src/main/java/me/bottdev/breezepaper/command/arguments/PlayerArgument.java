package me.bottdev.breezepaper.command.arguments;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.argument.CommandArgument;
import me.bottdev.breezemc.entity.player.BreezeOnlinePlayer;

@Getter
@RequiredArgsConstructor
public class PlayerArgument implements CommandArgument<BreezeOnlinePlayer> {

    private final String name;

    @Override
    public Class<BreezeOnlinePlayer> getType() {
        return BreezeOnlinePlayer.class;
    }

}
