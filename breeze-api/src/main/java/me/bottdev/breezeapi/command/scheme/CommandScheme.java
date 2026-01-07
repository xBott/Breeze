package me.bottdev.breezeapi.command.scheme;

import lombok.Getter;
import me.bottdev.breezeapi.command.CommandExecutionContext;
import me.bottdev.breezeapi.command.annotations.Argument;
import me.bottdev.breezeapi.command.annotations.Sender;
import me.bottdev.breezeapi.command.exceptions.SubCommandResolveException;
import me.bottdev.breezeapi.command.exceptions.UnsupportedParameterException;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class CommandScheme {

    public static CommandScheme ofMethod(Method method) throws SubCommandResolveException {
        CommandScheme scheme = new CommandScheme();

        for (Parameter parameter : method.getParameters()) {

            if (parameter.isAnnotationPresent(Sender.class)) {
                scheme.addResolver(CommandExecutionContext::getSender);
                continue;
            }

            if (parameter.isAnnotationPresent(Argument.class)) {
                Argument argumentAnnotation = parameter.getAnnotation(Argument.class);
                String name = argumentAnnotation.name();
                scheme.addResolver(new ArgumentSchemeResolver(name));
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
            Object object = resolvers.get(i).resolve(context);
            if (object == null) {
                throw new SubCommandResolveException("Resolver №" + i + " returned null");
            }
            objects[i] = object;
        }

        return objects;
    }

}
