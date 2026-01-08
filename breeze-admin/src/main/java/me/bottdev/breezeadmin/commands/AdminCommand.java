package me.bottdev.breezeadmin.commands;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.Command;
import me.bottdev.breezeapi.command.CommandSender;
import me.bottdev.breezeapi.command.annotations.Argument;
import me.bottdev.breezeapi.command.annotations.Sender;
import me.bottdev.breezeapi.command.annotations.SubCommand;
import me.bottdev.breezeapi.command.argument.CommandArgumentFactory;
import me.bottdev.breezeapi.command.argument.suggestion.SuggestionFactory;
import me.bottdev.breezeapi.command.argument.suggestion.SuggestionProvider;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezemc.entity.player.BreezeOnlinePlayer;
import me.bottdev.breezepaper.components.PaperPlayerManager;
import me.bottdev.breezepaper.entity.player.PaperOnlinePlayer;

@Component
public class AdminCommand implements Command {

    @RequiredArgsConstructor
    public static class PlayerSuggestionFactory implements SuggestionFactory {

        private final PaperPlayerManager paperPlayerManager;

        @Override
        public SuggestionProvider create() {
            return () -> paperPlayerManager.getPlayers().stream().map(PaperOnlinePlayer::getName).toList();
        }
    }

    @Inject
    public AdminCommand(
            CommandArgumentFactory commandArgumentFactory,
            PaperPlayerManager paperPlayerManager
    ) {
        commandArgumentFactory.registerSuggestionFactory(new PlayerSuggestionFactory(paperPlayerManager));
    }

    @Override
    public String getName() {
        return "admin";
    }

    @SubCommand()
    public void root(
            @Sender CommandSender sender
    ) {
        sender.send("Root command executed.");
    }

    @SubCommand(path = "ban <player>")
    public void ban(
            @Sender CommandSender sender,
            @Argument(name = "player", required = false) BreezeOnlinePlayer player
    ) {
        if (player == null) {
            sender.send("This player is not online.");
            return;
        }
        sender.send("Banning {}", player.getName());
    }

}
