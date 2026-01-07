package me.bottdev.breezeapi.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.nodes.CommandArgumentNode;
import me.bottdev.breezeapi.command.nodes.CommandExecuteNode;
import me.bottdev.breezeapi.command.nodes.CommandLiteralNode;
import me.bottdev.breezeapi.command.nodes.CommandRootNode;
import me.bottdev.breezeapi.command.nodes.execute.SimpleExecuteNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class CommandTreeBuilder<T extends CommandNode> {

    public static CommandTreeBuilder<CommandRootNode> named(String name) {
        CommandRootNode node = new CommandRootNode(name);
        return new CommandTreeBuilder<>(node);
    }

    public static CommandTreeBuilder<CommandLiteralNode> literal(String name) {
        return literal(new CommandLiteralNode(name));
    }

    public static CommandTreeBuilder<CommandLiteralNode> literal(CommandLiteralNode node) {
        return new CommandTreeBuilder<>(node);
    }

    public static CommandTreeBuilder<CommandArgumentNode> argument(CommandArgumentNode node) {
        return new CommandTreeBuilder<>(node);
    }

    public static CommandTreeBuilder<CommandExecuteNode> executes(Consumer<CommandExecutionContext> handler) {
        return executesNode(new SimpleExecuteNode(handler));
    }

    public static CommandTreeBuilder<CommandExecuteNode> executesNode(CommandExecuteNode node) {
        return new CommandTreeBuilder<>(node);
    }

    private final T node;
    private final List<CommandTreeBuilder<?>> children = new ArrayList<>();

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    private void addChild(CommandTreeBuilder<?> child) {
        children.add(child);
    }

    public CommandTreeBuilder<T> next(CommandTreeBuilder<?> builder) {
        addChild(builder);
        return this;
    }

    public T build() {

        if (hasChildren()) {
            children.forEach(builder -> {
                builder.build();
                node.addChild(builder.getNode());
            });

        }

        return node;
    }

}
