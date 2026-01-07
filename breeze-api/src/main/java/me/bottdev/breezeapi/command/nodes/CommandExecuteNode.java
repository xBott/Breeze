package me.bottdev.breezeapi.command.nodes;

import me.bottdev.breezeapi.command.CommandExecutionContext;
import me.bottdev.breezeapi.command.CommandNode;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public interface CommandExecuteNode extends CommandNode {

    @Override
    default String getValue() {
        return "execute";
    }

    @Override
    default String getDisplayName() {
        return "execute!";
    }

    @Override
    default Map<String, CommandNode> getChildren() {
        return Collections.emptyMap();
    }

    @Override
    default Optional<CommandNode> getChild(String value) {
        return Optional.empty();
    }

    void execute(CommandExecutionContext context);

}
