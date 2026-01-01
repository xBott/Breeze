package me.bottdev.breezeapi.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class CommandExecutionContext {

    private final CommandSender sender;
    private final Map<String, Object> arguments = new HashMap<>();

    public Object getArgument(String name) {
        return arguments.get(name);
    }

    public void setArgument(String name, Object value) {
        arguments.put(name, value);
    }

}

