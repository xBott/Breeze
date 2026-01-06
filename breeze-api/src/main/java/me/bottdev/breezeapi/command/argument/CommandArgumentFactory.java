package me.bottdev.breezeapi.command.argument;

import me.bottdev.breezeapi.command.annotations.Argument;
import me.bottdev.breezeapi.command.annotations.Ranged;
import me.bottdev.breezeapi.command.argument.types.BooleanArgument;
import me.bottdev.breezeapi.command.argument.types.FloatArgument;
import me.bottdev.breezeapi.command.argument.types.IntegerArgument;
import me.bottdev.breezeapi.command.argument.types.StringArgument;

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
                .register(String.class, (name, parameter) ->
                        new StringArgument(name)
                )

                .register(List.of(Integer.class, int.class), (name, parameter) -> {
                    if (parameter.isAnnotationPresent(Ranged.class)) {
                        Ranged ranged = parameter.getAnnotation(Ranged.class);
                        return new IntegerArgument(name, (int)ranged.min(), (int)ranged.max());
                    }
                    return new IntegerArgument(name);
                })

                .register(List.of(Float.class, float.class), (name, parameter) -> {
                    if (parameter.isAnnotationPresent(Ranged.class)) {
                        Ranged ranged = parameter.getAnnotation(Ranged.class);
                        return new FloatArgument(name, (float)ranged.min(), (float)ranged.max());
                    }
                    return new FloatArgument(name);
                })

                .register(List.of(Boolean.class, boolean.class), (name, parameter) ->
                        new BooleanArgument(name)
                );
    }

    private final Map<Class<?>, Factory> factories = new HashMap<>();

    public CommandArgumentFactory register(Class<?> type, Factory factory) {
        factories.put(type, factory);
        return this;
    }

    public CommandArgumentFactory register(List<Class<?>> types, Factory factory) {
        types.forEach(type -> register(type, factory));
        return this;
    }

    public Optional<CommandArgument<?>> create(String argumentName, Method method) {

        for (Parameter parameter : method.getParameters()) {

            if (!parameter.isAnnotationPresent(Argument.class)) continue;

            Argument argumentAnnotation = parameter.getAnnotation(Argument.class);
            String name = argumentAnnotation.name();

            if (!name.equalsIgnoreCase(argumentName)) continue;

            Class<?> type = parameter.getType();

            Factory factory = factories.get(type);
            if (factory == null) continue;

            CommandArgument<?> argument = factory.create(argumentName, parameter);
            return Optional.of(argument);

        }

        return Optional.empty();

    }

}
