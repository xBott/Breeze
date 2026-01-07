package me.bottdev.breezepaper.stages;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.Command;
import me.bottdev.breezeapi.command.CommandTreeParser;
import me.bottdev.breezeapi.command.argument.CommandArgumentFactory;
import me.bottdev.breezeapi.command.nodes.CommandArgumentNode;
import me.bottdev.breezeapi.command.nodes.CommandLiteralNode;
import me.bottdev.breezeapi.command.nodes.execute.MethodExecuteNode;
import me.bottdev.breezecore.StagedBreezeEngine;
import me.bottdev.breezecore.staged.ProcessStage;
import me.bottdev.breezepaper.autoloaders.PaperCommandAutoLoader;
import me.bottdev.breezepaper.command.PaperCommandContextFactory;
import me.bottdev.breezepaper.command.PaperCommandRegistrar;
import me.bottdev.breezepaper.command.nodes.PaperArgumentNodeFactory;
import me.bottdev.breezepaper.command.nodes.PaperExecuteNodeFactory;
import me.bottdev.breezepaper.command.nodes.PaperLiteralNodeFactory;
import me.bottdev.breezepaper.components.PaperPlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

@RequiredArgsConstructor
public class PaperCommandLoaderStage implements ProcessStage {

    private final JavaPlugin plugin;

    @Override
    public String getName() {
        return "Paper command loader";
    }

    @Override
    public void process(StagedBreezeEngine engine) {
        engine.getContext().get(PaperPlayerManager.class).ifPresent(paperPlayerManager -> {

            PaperCommandContextFactory contextFactory = new PaperCommandContextFactory(paperPlayerManager);

            PaperCommandRegistrar registrar = new PaperCommandRegistrar(plugin);
            registrar.addFactory(CommandLiteralNode.class, new PaperLiteralNodeFactory());
            registrar.addFactory(CommandArgumentNode.class, new PaperArgumentNodeFactory()
                    .addFactory(String.class, new PaperArgumentNodeFactory.Factory.Str())
                    .addFactory(Boolean.class, new PaperArgumentNodeFactory.Factory.Bool())
                    .addFactory(Integer.class, new PaperArgumentNodeFactory.Factory.Int())
                    .addFactory(Float.class, new PaperArgumentNodeFactory.Factory.Float())
            );
            registrar.addFactory(MethodExecuteNode.class, new PaperExecuteNodeFactory(contextFactory));

            CommandArgumentFactory argumentFactory = CommandArgumentFactory.defaultFactory();
            CommandTreeParser parser = new CommandTreeParser(argumentFactory);

            engine.getAutoLoaderRegistry().register(Command.class, new PaperCommandAutoLoader(parser, registrar));

        });
    }
}
