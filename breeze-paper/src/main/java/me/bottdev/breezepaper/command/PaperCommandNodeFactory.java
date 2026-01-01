package me.bottdev.breezepaper.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.bottdev.breezeapi.command.CommandNode;

@FunctionalInterface
public interface PaperCommandNodeFactory {
    ArgumentBuilder<CommandSourceStack, ?> create(ArgumentBuilder<CommandSourceStack, ?> parent, CommandNode node);

}
