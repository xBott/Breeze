package me.bottdev.breezepaper;

import me.bottdev.breezeapi.command.Command;
import me.bottdev.breezeapi.command.CommandSender;
import me.bottdev.breezeapi.command.annotations.Sender;
import me.bottdev.breezeapi.command.annotations.SubCommand;
import me.bottdev.breezeapi.di.annotations.Component;

@Component
public class BreezeCommand implements Command {

    @Override
    public String getName() {
        return "breeze";
    }

    @SubCommand(path = "help")
    public void help(
            @Sender CommandSender sender
    ) {
        sender.send("help / version / modules");
    }

    @SubCommand(path = "version")
    public void version(
            @Sender CommandSender sender
    ) {
        sender.send("Version of BreezePaper is ....");
    }

    @SubCommand(path = "modules")
    public void modules(
            @Sender CommandSender sender
    ) {
        sender.send("modules list / reload");
    }

    @SubCommand(path = "modules list")
    public void modulesList(
            @Sender CommandSender sender
    ) {
        sender.send("Here's a list of modules....");
    }

    @SubCommand(path = "modules reload")
    public void modulesReload(
            @Sender CommandSender sender
    ) {
        sender.send("Modules are reloaded!");
    }

}
