package me.bottdev.breezeapi.command.argument;

import me.bottdev.breezeapi.command.annotations.Argument;
import me.bottdev.breezeapi.command.annotations.Ranged;
import me.bottdev.breezeapi.command.argument.suggestion.SuggestionProvider;
import me.bottdev.breezeapi.command.argument.suggestion.types.EmptySuggestionFactory;
import me.bottdev.breezeapi.command.argument.types.BooleanArgument;
import me.bottdev.breezeapi.command.argument.types.FloatArgument;
import me.bottdev.breezeapi.command.argument.types.IntegerArgument;
import me.bottdev.breezeapi.command.argument.types.StringArgument;
import me.bottdev.breezeapi.command.argument.suggestion.SuggestionFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandArgumentFactory {

    @FunctionalInterface
    public interface Factory {
        CommandArgument<?> create(String name, Parameter parameter);
    }

    public static CommandArgumentFactory defaultFactory() {
        return new CommandArgumentFactory()
                .registerArgumentFactory(String.class, (name, parameter) ->
                        new StringArgument(name)
                )

                .registerArgumentFactory(List.of(Integer.class, int.class), (name, parameter) -> {
                    if (parameter.isAnnotationPresent(Ranged.class)) {
                        Ranged ranged = parameter.getAnnotation(Ranged.class);
                        return new IntegerArgument(name, (int)ranged.min(), (int)ranged.max());
                    }
                    return new IntegerArgument(name);
                })

                .registerArgumentFactory(List.of(Float.class, float.class), (name, parameter) -> {
                    if (parameter.isAnnotationPresent(Ranged.class)) {
                        Ranged ranged = parameter.getAnnotation(Ranged.class);
                        return new FloatArgument(name, (float)ranged.min(), (float)ranged.max());
                    }
                    return new FloatArgument(name);
                })

                .registerArgumentFactory(List.of(Boolean.class, boolean.class), (name, parameter) ->
                        new BooleanArgument(name)
                )
                .registerSuggestionFactory(new EmptySuggestionFactory());
    }

    private final Map<Class<?>, Factory> argumentFactories = new HashMap<>();
    private final Map<Class<? extends SuggestionFactory>, SuggestionFactory> suggestionFactories = new HashMap<>();

    public CommandArgumentFactory registerArgumentFactory(Class<?> type, Factory factory) {
        argumentFactories.put(type, factory);
        return this;
    }

    public CommandArgumentFactory registerArgumentFactory(List<Class<?>> types, Factory factory) {
        types.forEach(type -> registerArgumentFactory(type, factory));
        return this;
    }

    private Optional<Factory> getArgumentFactory(Class<?> type) {
        return Optional.ofNullable(argumentFactories.get(type));
    }

    public CommandArgumentFactory registerSuggestionFactory(
            SuggestionFactory factory
    ) {
        suggestionFactories.put(factory.getClass(), factory);
        return this;
    }

    private Optional<SuggestionFactory> getSuggestionFactory(Class<? extends SuggestionFactory> type) {
        return Optional.ofNullable(suggestionFactories.get(type));
    }

    public Optional<CommandArgument<?>> create(String argumentName, Method method) {

        for (Parameter parameter : method.getParameters()) {

            if (!parameter.isAnnotationPresent(Argument.class)) continue;

            Argument argumentAnnotation = parameter.getAnnotation(Argument.class);
            String name = argumentAnnotation.name();
            Class<? extends SuggestionFactory> suggestionFactoryClazz = argumentAnnotation.suggest();

            if (!name.equalsIgnoreCase(argumentName)) continue;

            Class<?> type = parameter.getType();

            Optional<Factory> factoryOptional = getArgumentFactory(type);
            if (factoryOptional.isEmpty()) continue;
            Factory factory = factoryOptional.get();

            CommandArgument<?> argument = factory.create(argumentName, parameter);
            if (argument instanceof Suggestable suggestable) {
                addSuggestions(suggestable, suggestionFactoryClazz);
            }

            return Optional.of(argument);

        }

        return Optional.empty();

    }

    private void addSuggestions(Suggestable suggestable, Class<? extends SuggestionFactory> suggestionFactoryClazz) {
        getSuggestionFactory(suggestionFactoryClazz).ifPresent(factory -> {
            SuggestionProvider suggestionProvider = factory.create();
            suggestable.setSuggestionProvider(suggestionProvider);
        });
    }

}
