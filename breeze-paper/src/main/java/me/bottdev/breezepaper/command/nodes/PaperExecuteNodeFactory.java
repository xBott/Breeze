package me.bottdev.breezepaper.command.nodes;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.CommandNode;
import me.bottdev.breezeapi.command.nodes.execute.MethodExecuteNode;
import me.bottdev.breezepaper.command.PaperCommandContextFactory;
import me.bottdev.breezepaper.command.PaperCommandNodeFactory;

import java.util.Optional;

@RequiredArgsConstructor
public class PaperExecuteNodeFactory implements PaperCommandNodeFactory {

    private final PaperCommandContextFactory contextFactory;

    @Override
    public Optional<ArgumentBuilder<CommandSourceStack, ?>> create(ArgumentBuilder<CommandSourceStack, ?> parent, CommandNode node) {

        MethodExecuteNode executeNode = (MethodExecuteNode) node;

        return Optional.of(parent.executes(paperContext -> {
            contextFactory.create(executeNode, paperContext).ifPresent(executeNode::execute);
            return 1;
        }));

    }

}
