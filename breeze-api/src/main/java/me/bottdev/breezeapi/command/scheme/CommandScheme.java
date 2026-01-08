package me.bottdev.breezeapi.command.scheme;

import lombok.Getter;
import me.bottdev.breezeapi.command.CommandExecutionContext;
import me.bottdev.breezeapi.command.annotations.Argument;
import me.bottdev.breezeapi.command.annotations.Sender;
import me.bottdev.breezeapi.command.exceptions.SubCommandResolveException;
import me.bottdev.breezeapi.command.exceptions.UnsupportedParameterException;
import me.bottdev.breezeapi.command.scheme.resolvers.ArgumentSchemeResolver;
import me.bottdev.breezeapi.command.scheme.resolvers.SenderSchemeResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class CommandScheme {

    public static CommandScheme ofMethod(Method method) throws SubCommandResolveException {
        CommandScheme scheme = new CommandScheme();

        for (Parameter parameter : method.getParameters()) {

            if (parameter.isAnnotationPresent(Sender.class)) {
                scheme.addResolver(new SenderSchemeResolver());
                continue;
            }

            Class<?> type = parameter.getType();
            if (parameter.isAnnotationPresent(Argument.class)) {
                Argument argumentAnnotation = parameter.getAnnotation(Argument.class);
                String name = argumentAnnotation.name();
                boolean required = argumentAnnotation.required();
                scheme.addResolver(new ArgumentSchemeResolver(name, type, required));
                continue;
            }

            throw new UnsupportedParameterException(parameter.getName());
        }

        return scheme;
    }

    @Getter
    private final List<SchemeResolver> resolvers = new ArrayList<>();

    public void addResolver(SchemeResolver resolver) {
        resolvers.add(resolver);
    }

    public Object[] resolve(CommandExecutionContext context) throws SubCommandResolveException {
        Object[] objects = new Object[resolvers.size()];

        for (int i = 0; i < resolvers.size(); i++) {
            SchemeResolver resolver = resolvers.get(i);
            Object object = resolver.resolve(context);
            if (object == null && resolver.isRequired()) {
                throw new SubCommandResolveException("Failed to resolve sub-command argument at position " + i);
            }
            objects[i] = object;
        }

        return objects;
    }

}
