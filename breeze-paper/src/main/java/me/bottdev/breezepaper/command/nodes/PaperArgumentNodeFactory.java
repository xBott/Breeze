package me.bottdev.breezepaper.command.nodes;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import me.bottdev.breezeapi.command.CommandNode;
import me.bottdev.breezeapi.command.argument.CommandArgument;
import me.bottdev.breezeapi.command.argument.types.BooleanArgument;
import me.bottdev.breezeapi.command.argument.types.FloatArgument;
import me.bottdev.breezeapi.command.argument.types.IntegerArgument;
import me.bottdev.breezeapi.command.nodes.CommandArgumentNode;
import me.bottdev.breezepaper.command.PaperCommandNodeFactory;

public class PaperArgumentNodeFactory implements PaperCommandNodeFactory {

    @FunctionalInterface
    public interface Factory {
        ArgumentBuilder<CommandSourceStack, ?> create(CommandArgument<?> argument);
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> create(ArgumentBuilder<CommandSourceStack, ?> parent, CommandNode node) {

        CommandArgumentNode argumentNode = (CommandArgumentNode) node;

        CommandArgument<?> argument = argumentNode.getArgument();
        Class<?> type = argument.getType();

        ArgumentBuilder<CommandSourceStack, ?> paperNode;

        if (Integer.class.isAssignableFrom(type)) {
            paperNode = createIntegerArgument((IntegerArgument) argument);

        } else {
            paperNode = Commands.literal(argument.getName());

        }

        return paperNode;

    }

    private ArgumentBuilder<CommandSourceStack, ?> createIntegerArgument(IntegerArgument argument) {
        String name = argument.getName();
        int min = argument.getMin();
        int max = argument.getMax();
        return Commands.argument(name, IntegerArgumentType.integer(min, max));
    }

    private ArgumentBuilder<CommandSourceStack, ?> createFloatArgument(FloatArgument argument) {
        String name = argument.getName();
        float min = argument.getMin();
        float max = argument.getMax();
        return Commands.argument(name, FloatArgumentType.floatArg(min, max));
    }

    private ArgumentBuilder<CommandSourceStack, ?> createBooleanArgument(BooleanArgument argument) {
        String name = argument.getName();
        return Commands.argument(name, BoolArgumentType.bool());
    }

    private ArgumentBuilder<CommandSourceStack, ?> createStringArgument(IntegerArgument argument) {
        String name = argument.getName();
        return Commands
                .argument(name, StringArgumentType.string()
                ).suggests((ctx, builder) -> {
                    builder.suggest("word");
                    return builder.buildFuture();
                }
        );
    }

}
