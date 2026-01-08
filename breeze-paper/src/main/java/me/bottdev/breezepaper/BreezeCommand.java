package me.bottdev.breezepaper;

import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.command.Command;
import me.bottdev.breezeapi.command.CommandSender;
import me.bottdev.breezeapi.command.annotations.Argument;
import me.bottdev.breezeapi.command.annotations.Sender;
import me.bottdev.breezeapi.command.annotations.SubCommand;
import me.bottdev.breezeapi.di.SupplyType;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.modules.ModuleDescriptor;
import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezeapi.modules.ModuleStatus;

import java.io.File;

@Component
public class BreezeCommand implements Command {

    private final BreezeEngine breezeEngine;
    private final ModuleManager moduleManager;

    @Inject
    public BreezeCommand(BreezeEngine breezeEngine, ModuleManager moduleManager) {
        this.breezeEngine = breezeEngine;
        this.moduleManager = moduleManager;
    }

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

    @SubCommand(path = "restart")
    public void modulesReload(
            @Sender CommandSender sender
    ) {
        breezeEngine.restart();
    }

    @SubCommand(path = "modules")
    public void modules(
            @Sender CommandSender sender
    ) {
        sender.send("use modules list");
    }

    @SubCommand(path = "modules list")
    public void modulesList(
            @Sender CommandSender sender
    ) {
        sender.send("List of modules:");
        moduleManager.getModules().forEach(module -> {
            ModuleDescriptor descriptor = module.getDescriptor();
            String name = descriptor.getName();
            String version = descriptor.getVersion();
            ModuleStatus status = module.getStatus();
            sender.send(" <gray>- <white>{} <yellow>{} <light_purple>{}", name, version, status);
        });
    }

    @SubCommand(path = "modules info <name>")
    public void moduleInfo(
            @Sender CommandSender sender,
            @Argument(name = "name", suggest = ModuleSuggestionFactory.class) String moduleName
    ) {

        moduleManager.getModules().stream()
                .filter(module -> module.getDescriptor().getName().equals(moduleName))
                .findFirst()
                .ifPresent(module -> {

                    ModuleDescriptor descriptor = module.getDescriptor();
                    String name = descriptor.getName();
                    String version = descriptor.getVersion();
                    ModuleStatus status = module.getStatus();
                    File dataFolder = module.getDataFolder();

                    sender.send("Information about module <light_purple>{}<white>:", name);
                    sender.send("  <gray>Status: <yellow>{}", status);
                    sender.send("  <gray>Version: <yellow>{}", version);
                    sender.send("  <gray>Data Folder: <yellow>{}>",  dataFolder.getAbsolutePath());

                });

    }

    @SubCommand(path = "di info")
    public void diInfo(
            @Sender CommandSender sender
    ) {

        sender.send("<white>List of all suppliers:");
        breezeEngine.getContext().getSuppliers().forEach((key, supplier) -> {
            Object object = supplier.supply();
            SupplyType supplyType = supplier.getType();
            String className = object.getClass().getSimpleName();
            sender.send("  <yellow>{} <gray>{}: <light_purple>{}", supplyType, key, className);
        });

    }

}
