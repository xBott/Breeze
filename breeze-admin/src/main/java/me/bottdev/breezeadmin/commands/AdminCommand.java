package me.bottdev.breezeadmin.commands;

import me.bottdev.breezeapi.command.Command;
import me.bottdev.breezeapi.command.CommandSender;
import me.bottdev.breezeapi.command.annotations.Sender;
import me.bottdev.breezeapi.command.annotations.SubCommand;
import me.bottdev.breezeapi.di.annotations.Component;

@Component
public class AdminCommand implements Command {

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

}
