package me.bottdev.breezeapi.command.argument.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.argument.CommandArgument;

@RequiredArgsConstructor
public class BooleanArgument implements CommandArgument<Boolean> {

    @Getter
    private final String name;

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

}
