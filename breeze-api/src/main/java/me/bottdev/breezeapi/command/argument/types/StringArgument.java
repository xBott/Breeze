package me.bottdev.breezeapi.command.argument.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.argument.CommandArgument;

@RequiredArgsConstructor
public class StringArgument implements CommandArgument<String> {

    @Getter
    private final String name;

    @Override
    public Class<String> getType() {
        return String.class;
    }

}
