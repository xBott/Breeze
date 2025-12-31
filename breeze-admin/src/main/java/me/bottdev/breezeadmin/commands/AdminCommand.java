package me.bottdev.breezeadmin.commands;

import me.bottdev.breezeadmin.AdminModule;
import me.bottdev.breezeapi.command.Command;
import me.bottdev.breezeapi.command.annotations.SubCommand;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;

@Component
public class AdminCommand implements Command {

    @Inject
    private AdminModule module;

    @Override
    public String getName() {
        return "admin";
    }

    @SubCommand()
    public void root() {
        module.getLogger().info("Root command executed.");
    }

}
