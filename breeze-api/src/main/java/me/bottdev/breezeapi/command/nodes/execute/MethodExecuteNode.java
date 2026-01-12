package me.bottdev.breezeapi.command.nodes.execute;

import lombok.Getter;
import me.bottdev.breezeapi.command.CommandExecutionContext;
import me.bottdev.breezeapi.command.exceptions.SubCommandResolveException;
import me.bottdev.breezeapi.command.exceptions.UnsupportedParameterException;
import me.bottdev.breezeapi.command.nodes.CommandExecuteNode;
import me.bottdev.breezeapi.command.scheme.CommandScheme;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.platforms.SL4JLogPlatform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Getter
public class MethodExecuteNode implements CommandExecuteNode {

    private final BreezeLogger logger = SL4JLogPlatform.getFactory().simple("CommandExecuteNode");

    private final Object commandInstance;
    private final Method method;
    private final CommandScheme scheme;

    public MethodExecuteNode(Object commandInstance, Method handler) throws UnsupportedParameterException {
        this.commandInstance = commandInstance;
        this.method = handler;
        this.scheme = CommandScheme.ofMethod(handler);
    }

    @Override
    public void execute(CommandExecutionContext context) {
        try {
            Object[] args = scheme.resolve(context);
            method.invoke(commandInstance, args);

        } catch (SubCommandResolveException | IllegalAccessException | InvocationTargetException ex) {
            logger.error("Failed to execute command", ex);

        }
    }

}
