package me.bottdev.breezepaper.command.nodes;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import me.bottdev.breezeapi.command.CommandNode;
import me.bottdev.breezeapi.command.argument.CommandArgument;
import me.bottdev.breezeapi.command.argument.Suggestable;
import me.bottdev.breezeapi.command.argument.types.FloatArgument;
import me.bottdev.breezeapi.command.argument.types.IntegerArgument;
import me.bottdev.breezeapi.command.nodes.CommandArgumentNode;
import me.bottdev.breezeapi.command.argument.suggestion.SuggestionProvider;
import me.bottdev.breezepaper.command.PaperCommandNodeFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PaperArgumentNodeFactory implements PaperCommandNodeFactory {

    @FunctionalInterface
    public interface Factory {

        class Str implements Factory {

            @Override
            public RequiredArgumentBuilder<CommandSourceStack, ?> create(CommandArgument<?> argument) {
                String name = argument.getName();
                return Commands.argument(name, StringArgumentType.word());
            }

        }

        class Bool implements Factory {

            @Override
            public RequiredArgumentBuilder<CommandSourceStack, ?> create(CommandArgument<?> argument) {
                String name = argument.getName();
                return Commands.argument(name, BoolArgumentType.bool());
            }

        }

        class Int implements Factory {

            @Override
            public RequiredArgumentBuilder<CommandSourceStack, ?> create(CommandArgument<?> argument) {
                IntegerArgument integerArgument = (IntegerArgument) argument;
                String name = argument.getName();
                int min = integerArgument.getMin();
                int max = integerArgument.getMax();
                return Commands.argument(name, IntegerArgumentType.integer(min, max));
            }

        }

        class Float implements Factory {

            @Override
            public RequiredArgumentBuilder<CommandSourceStack, ?> create(CommandArgument<?> argument) {
                FloatArgument integerArgument = (FloatArgument) argument;
                String name = argument.getName();
                float min = integerArgument.getMin();
                float max = integerArgument.getMax();
                return Commands.argument(name, FloatArgumentType.floatArg(min, max));
            }

        }

        class Player implements Factory {

            @Override
            public RequiredArgumentBuilder<CommandSourceStack, ?> create(CommandArgument<?> argument) {
                return Commands.argument(argument.getName(), ArgumentTypes.player());
            }

        }

        RequiredArgumentBuilder<CommandSourceStack, ?> create(CommandArgument<?> argument);
    }

    private final Map<Class<?>, Factory> factories = new HashMap<>();

    public PaperArgumentNodeFactory addFactory(Class<?> type, Factory factory) {
        factories.put(type, factory);
        return this;
    }

    private Optional<Factory> getFactory(Class<?> type) {
        return Optional.ofNullable(factories.get(type));
    }

    @Override
    public Optional<ArgumentBuilder<CommandSourceStack, ?>> create(ArgumentBuilder<CommandSourceStack, ?> parent, CommandNode node) {

        CommandArgumentNode argumentNode = (CommandArgumentNode) node;

        CommandArgument<?> argument = argumentNode.getArgument();
        Class<?> type = argument.getType();

        return getFactory(type).map(factory -> {

            RequiredArgumentBuilder<CommandSourceStack, ?> paperNode = factory.create(argument);

            if (argument instanceof Suggestable suggestable) {
                suggestable.getSuggestionProvider().ifPresent(provider ->
                        addSuggestions(paperNode, provider)
                );
            }

            return paperNode;

        });

    }

    private void addSuggestions(RequiredArgumentBuilder<CommandSourceStack, ?> argumentBuilder, SuggestionProvider provider) {

        argumentBuilder.suggests((ctx, builder) -> {
            provider.provide().forEach(builder::suggest);
            return builder.buildFuture();
        });

    }

}
