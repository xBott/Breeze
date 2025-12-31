package me.bottdev.breezeapi.command;

import me.bottdev.breezeapi.command.annotations.SubCommand;
import me.bottdev.breezeapi.command.argument.CommandArgument;
import me.bottdev.breezeapi.command.argument.CommandArgumentFactory;
import me.bottdev.breezeapi.command.argument.types.BooleanArgument;
import me.bottdev.breezeapi.command.argument.types.FloatArgument;
import me.bottdev.breezeapi.command.argument.types.IntegerArgument;
import me.bottdev.breezeapi.command.argument.types.StringArgument;
import me.bottdev.breezeapi.command.nodes.CommandArgumentNode;
import me.bottdev.breezeapi.command.nodes.CommandExecuteNode;
import me.bottdev.breezeapi.command.nodes.CommandLiteralNode;
import me.bottdev.breezeapi.command.nodes.CommandRootNode;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class CommandTreeParser {

    private final CommandArgumentFactory argumentFactory = new CommandArgumentFactory()
            .register(String.class, StringArgument::new)
            .register(int.class, IntegerArgument::new)
            .register(Integer.class, IntegerArgument::new)
            .register(float.class, FloatArgument::new)
            .register(Float.class, FloatArgument::new)
            .register(boolean.class, BooleanArgument::new)
            .register(Boolean.class, BooleanArgument::new);

    public CommandRootNode parse(Command command) throws IllegalArgumentException {

        String commandName = command.getName();
        CommandRootNode rootNode = new CommandRootNode(commandName);

        Arrays.stream(command.getClass().getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(SubCommand.class))
                .forEach(method -> parseSubCommand(command, rootNode, method));

        printTree(0, rootNode);

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

        System.out.println("Parsing sub-command " + path);

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

                String argumentName = part.substring(1, part.length() - 1);
                Optional<CommandArgument<?>> argumentOptional = argumentFactory.create(argumentName, method);
                if (argumentOptional.isEmpty()) {
                    throw new IllegalArgumentException("Failed to parse sub-command \"" + path + "\". Could not find argument " + argumentName);
                }
                CommandArgument<?> argument = argumentOptional.get();

                newNode = new CommandArgumentNode(argument);

            } else {
                newNode = new CommandLiteralNode(part);
            }

            current.addChild(newNode);
            current = newNode;
            index++;

        }

        if (current instanceof CommandExecuteNode) {
            return;
        }

        CommandExecuteNode executeNode = new CommandExecuteNode(command, method);
        current.addChild(executeNode);

    }

    public void printTree(int indent, CommandNode node) {

        System.out.println(
                new StringBuilder()
                    .append("    ".repeat(indent))
                    .append("- ")
                    .append(node.getDisplayName())
        );

        for (CommandNode child : node.getChildren().values()) {
            printTree(indent + 1, child);
        }

    }


}
