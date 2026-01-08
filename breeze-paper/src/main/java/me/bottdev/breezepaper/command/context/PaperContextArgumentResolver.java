package me.bottdev.breezepaper.command.context;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.Optional;

@FunctionalInterface
public interface PaperContextArgumentResolver<T> {

    Optional<T> resolve(String name, CommandContext<CommandSourceStack> paperContext);

}
