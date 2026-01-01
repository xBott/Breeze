package me.bottdev.breezepaper.command.nodes;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.bottdev.breezeapi.command.CommandNode;
import me.bottdev.breezeapi.command.nodes.CommandLiteralNode;
import me.bottdev.breezepaper.command.PaperCommandNodeFactory;

public class PaperLiteralNodeFactory implements PaperCommandNodeFactory {

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> create(ArgumentBuilder<CommandSourceStack, ?> parent, CommandNode node) {
        CommandLiteralNode literalNode = (CommandLiteralNode) node;
        String value = literalNode.getValue();
        return Commands.literal(value);
    }

}
