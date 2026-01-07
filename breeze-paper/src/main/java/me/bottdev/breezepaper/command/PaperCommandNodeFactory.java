package me.bottdev.breezepaper.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.bottdev.breezeapi.command.CommandNode;

import java.util.Optional;

@FunctionalInterface
public interface PaperCommandNodeFactory {
    Optional<ArgumentBuilder<CommandSourceStack, ?>> create(ArgumentBuilder<CommandSourceStack, ?> parent, CommandNode node);

}
