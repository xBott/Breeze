package me.bottdev.breezeapi.command.nodes;

import lombok.Getter;
import me.bottdev.breezeapi.command.CommandExecutionContext;
import me.bottdev.breezeapi.command.CommandNode;
import me.bottdev.breezeapi.command.exceptions.SubCommandResolveException;
import me.bottdev.breezeapi.command.exceptions.UnsupportedParameterException;
import me.bottdev.breezeapi.command.scheme.CommandScheme;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Getter
public class CommandExecuteNode implements CommandNode {

    private final BreezeLogger logger = new SimpleLogger("CommandExecuteNode");

    private final Object commandInstance;
    private final Method method;
    private final CommandScheme scheme;

    public CommandExecuteNode(Object commandInstance, Method handler) throws UnsupportedParameterException {
        this.commandInstance = commandInstance;
        this.method = handler;
        this.scheme = CommandScheme.ofMethod(handler);
    }

    @Override
    public String getValue() {
        return "execute";
    }

    @Override
    public String getDisplayName() {
        return "execute!";
    }

    @Override
    public Map<String, CommandNode> getChildren() {
        return Collections.emptyMap();
    }

    @Override
    public Optional<CommandNode> getChild(String value) {
        return Optional.empty();
    }

    public void execute(CommandExecutionContext context) {
        try {
            Object[] args = scheme.resolve(context);
            method.invoke(commandInstance, args);

        } catch (SubCommandResolveException | IllegalAccessException | InvocationTargetException ex) {
            logger.error("Failed to execute command", ex);

        }
    }

}
