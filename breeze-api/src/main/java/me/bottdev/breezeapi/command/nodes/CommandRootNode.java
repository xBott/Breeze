package me.bottdev.breezeapi.command.nodes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.CommandNode;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class CommandRootNode implements CommandNode {

    private final String name;
    private final Map<String, CommandNode> children = new HashMap<>();

    @Override
    public String getValue() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return name +  " (root)";
    }

}
