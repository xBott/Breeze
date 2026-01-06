package me.bottdev.breezepaper;

import lombok.Getter;
import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.command.Command;
import me.bottdev.breezeapi.command.CommandTreeParser;
import me.bottdev.breezeapi.command.argument.CommandArgumentFactory;
import me.bottdev.breezeapi.command.nodes.CommandArgumentNode;
import me.bottdev.breezeapi.command.nodes.CommandExecuteNode;
import me.bottdev.breezeapi.command.nodes.CommandLiteralNode;
import me.bottdev.breezeapi.i18n.TranslationModuleManager;
import me.bottdev.breezeapi.log.TreeLogger;
import me.bottdev.breezeapi.modules.ModuleManager;
import me.bottdev.breezecore.modules.loaders.DependencyModuleLoader;
import me.bottdev.breezecore.SimpleBreezeEngine;
import me.bottdev.breezepaper.autoloaders.PaperCommandAutoLoader;
import me.bottdev.breezepaper.command.PaperCommandContextFactory;
import me.bottdev.breezepaper.command.PaperCommandRegistrar;
import me.bottdev.breezepaper.command.nodes.PaperArgumentNodeFactory;
import me.bottdev.breezepaper.command.nodes.PaperExecuteNodeFactory;
import me.bottdev.breezepaper.command.nodes.PaperLiteralNodeFactory;
import me.bottdev.breezepaper.entity.player.PlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public class BreezePaper extends JavaPlugin {

    @Getter
    private static BreezePaper instance;
    @Getter
    private BreezeEngine engine;
    @Getter
    private PlayerManager playerManager;

    @Override
    public void onEnable() {
        instance = this;

        engine = new SimpleBreezeEngine(getDataPath().toAbsolutePath(), () -> {

            TranslationModuleManager translationModuleManager =
                    engine.getContext().get(TranslationModuleManager.class).orElseThrow();
            playerManager = new PlayerManager(translationModuleManager);

            addDependencyModuleLoader();
            addCommandAutoloader();

        });

        engine.start();

        playerManager.getPlayers().forEach(player ->
                player.sendMessage("Location of player {} is {}", player.getName(), player.getLocation())
        );

    }

    private void addDependencyModuleLoader() {
        engine.getContext().get(ModuleManager.class).ifPresent(moduleManager ->
                engine.getContext().get(TreeLogger.class, "mainLogger").ifPresent(treeLogger -> {

                    ClassLoader parentClassLoader = getClassLoader();
                    Path directory = getDataFolder().toPath().resolve("modules");
                    DependencyModuleLoader loader =
                            new DependencyModuleLoader(engine.getLogger(), parentClassLoader, engine, directory);
                    moduleManager.addModuleLoader(loader);

                })
        );
    }

    private void addCommandAutoloader() {

        PaperCommandContextFactory contextFactory = new PaperCommandContextFactory(playerManager);

        PaperCommandRegistrar registrar = new PaperCommandRegistrar(this);
        registrar.addFactory(CommandLiteralNode.class, new PaperLiteralNodeFactory());
        registrar.addFactory(CommandArgumentNode.class, new PaperArgumentNodeFactory());
        registrar.addFactory(CommandExecuteNode.class, new PaperExecuteNodeFactory(contextFactory));

        CommandArgumentFactory argumentFactory = CommandArgumentFactory.defaultFactory();
        CommandTreeParser parser = new CommandTreeParser(argumentFactory);

        engine.getAutoLoaderRegistry().register(Command.class, new PaperCommandAutoLoader(parser, registrar));

    }

    @Override
    public void onDisable() {
        engine.stop();
    }

}
