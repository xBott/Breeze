package me.bottdev.breezeapi.command.argument;

import me.bottdev.breezeapi.command.annotations.Argument;
import me.bottdev.breezeapi.command.argument.types.BooleanArgument;
import me.bottdev.breezeapi.command.argument.types.FloatArgument;
import me.bottdev.breezeapi.command.argument.types.IntegerArgument;
import me.bottdev.breezeapi.command.argument.types.StringArgument;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class CommandArgumentFactory {

    public static CommandArgumentFactory defaultFactory() {
        return new CommandArgumentFactory()
                .register(String.class, StringArgument::new)
                .register(int.class, IntegerArgument::new)
                .register(Integer.class, IntegerArgument::new)
                .register(float.class, FloatArgument::new)
                .register(Float.class, FloatArgument::new)
                .register(boolean.class, BooleanArgument::new)
                .register(Boolean.class, BooleanArgument::new);
    }

    private final Map<Class<?>, Function<String, CommandArgument<?>>> factories = new HashMap<>();

    public CommandArgumentFactory register(Class<?> type, Function<String, CommandArgument<?>> factory) {
        factories.put(type, factory);
        return this;
    }

    public Optional<CommandArgument<?>> create(String argumentName, Method method) {

        for (Parameter parameter : method.getParameters()) {

            if (!parameter.isAnnotationPresent(Argument.class)) continue;

            Argument argumentAnnotation = parameter.getAnnotation(Argument.class);
            String name = argumentAnnotation.name();

            if (!name.equalsIgnoreCase(argumentName)) continue;

            Class<?> type = parameter.getType();

            Function<String, CommandArgument<?>> factory = factories.get(type);
            if (factory == null) continue;

            CommandArgument<?> argument = factory.apply(argumentName);
            return Optional.of(argument);

        }

        return Optional.empty();

    }

}
