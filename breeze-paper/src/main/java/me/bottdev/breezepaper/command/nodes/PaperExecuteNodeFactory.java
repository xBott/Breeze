package me.bottdev.breezepaper.command.nodes;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.CommandExecutionContext;
import me.bottdev.breezeapi.command.CommandNode;
import me.bottdev.breezeapi.command.nodes.CommandExecuteNode;
import me.bottdev.breezeapi.command.nodes.CommandLiteralNode;
import me.bottdev.breezepaper.command.PaperCommandContextFactory;
import me.bottdev.breezepaper.command.PaperCommandNodeFactory;

import java.util.Optional;

@RequiredArgsConstructor
public class PaperExecuteNodeFactory implements PaperCommandNodeFactory {

    private final PaperCommandContextFactory contextFactory;

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> create(ArgumentBuilder<CommandSourceStack, ?> parent, CommandNode node) {

        CommandExecuteNode executeNode = (CommandExecuteNode) node;

        return parent.executes(paperContext -> {
            contextFactory.create(executeNode, paperContext).ifPresent(executeNode::execute);
            return 1;
        });

    }

}
