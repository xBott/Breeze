package me.bottdev.breezepaper.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.bottdev.breezeapi.command.CommandNode;
import me.bottdev.breezeapi.command.nodes.CommandRootNode;
import org.bukkit.plugin.java.JavaPlugin;

public class PaperCommandRegistrar {

    public static void register(CommandRootNode rootNode, JavaPlugin plugin) {
        LiteralCommandNode<CommandSourceStack> paperRoot = convert(rootNode).build();
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(paperRoot);
        });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> convert(CommandRootNode rootNode) {
        LiteralArgumentBuilder<CommandSourceStack> paperRoot = Commands.literal(rootNode.getName());

        rootNode.getChildren().values().forEach(child ->
                registerSubCommand(child, paperRoot)
        );

        return paperRoot;
    }

    private static void registerSubCommand(CommandNode commandNode, LiteralArgumentBuilder<CommandSourceStack> paperParent) {

        if (!commandNode.hasChildren()) return;

        LiteralArgumentBuilder<CommandSourceStack> paperNode = Commands.literal(commandNode.getValue());
        paperParent.then(paperNode);

        commandNode.getChildren().values().forEach(child ->
                registerSubCommand(child, paperParent)
        );

    }

}
