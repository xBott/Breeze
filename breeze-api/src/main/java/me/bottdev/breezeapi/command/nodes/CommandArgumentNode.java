package me.bottdev.breezeapi.command.nodes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.CommandNode;
import me.bottdev.breezeapi.command.argument.CommandArgument;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CommandArgumentNode implements CommandNode {

    @Getter
    private final CommandArgument<?> argument;

    @Getter
    private final Map<String, CommandNode> children = new HashMap<>();

    @Override
    public String getValue() {
        return argument.getName();
    }

    @Override
    public String getDisplayName() {
        return "<" + argument.getName() + "> (argument:" + argument.getType().getSimpleName() + ")";
    }

}
