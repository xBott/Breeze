package me.bottdev.breezeapi.command;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.annotations.SubCommand;
import me.bottdev.breezeapi.command.argument.CommandArgument;
import me.bottdev.breezeapi.command.argument.CommandArgumentFactory;
import me.bottdev.breezeapi.command.nodes.CommandArgumentNode;
import me.bottdev.breezeapi.command.nodes.execute.MethodExecuteNode;
import me.bottdev.breezeapi.command.nodes.CommandLiteralNode;
import me.bottdev.breezeapi.command.nodes.CommandRootNode;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@RequiredArgsConstructor
public class CommandTreeParser {

    private final CommandArgumentFactory argumentFactory;

    public CommandRootNode parse(Command command) {

        String commandName = command.getName();
        CommandRootNode rootNode = new CommandRootNode(commandName);

        Arrays.stream(command.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(SubCommand.class))
                .forEach(method -> parseSubCommand(command, rootNode, method));

        return rootNode;
    }

    public void parseSubCommand(
            Command command,
            CommandRootNode rootNode,
            Method method
    ) throws IllegalArgumentException {

        SubCommand subCommand = method.getAnnotation(SubCommand.class);
        String path = subCommand.path();
        String trimmedPath = path.trim();

        String[] parts = trimmedPath.isEmpty() ? new String[]{} : trimmedPath.split(" ");
        int partCount = parts.length;

        CommandNode current = rootNode;

        int index = 0;
        for (String part : parts) {

            Optional<CommandNode> nextOptional = current.getChild(part);

            if (nextOptional.isPresent()) {
                current = nextOptional.get();
                index++;
                continue;
            }

            if (index == partCount) {
                break;
            }

            CommandNode newNode;

            if (part.startsWith("<") && part.endsWith(">")) {
                newNode = parseArgumentNode(part, method);
            } else {
                newNode = parseLiteralNode(part);
            }

            current.addChild(newNode);
            current = newNode;
            index++;

        }

        if (current instanceof MethodExecuteNode) {
            return;
        }

        current.addChild(parseExecuteNode(command, method));

    }

    private CommandNode parseArgumentNode(
            String part,
            Method method
    ) {
        String argumentName = part.substring(1, part.length() - 1);
        Optional<CommandArgument<?>> argumentOptional = argumentFactory.create(argumentName, method);
        if (argumentOptional.isEmpty()) {
            throw new IllegalArgumentException("Failed to parse sub-command. Could not find argument " + argumentName);
        }
        CommandArgument<?> argument = argumentOptional.get();

        return new CommandArgumentNode(argument);
    }

    private CommandNode parseLiteralNode(
            String part
    ) {
        return new CommandLiteralNode(part);
    }

    private CommandNode parseExecuteNode(
            Command command,
            Method method
    ) {
        return new MethodExecuteNode(command, method);
    }

}
