package me.bottdev.breezeapi.command.nodes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.CommandNode;

import java.lang.reflect.Method;
import java.util.*;

@Getter
@RequiredArgsConstructor
public class CommandExecuteNode implements CommandNode {

    private final Object commandInstance;
    private final Method handler;

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

}
