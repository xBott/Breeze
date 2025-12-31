package me.bottdev.breezeapi.command.argument.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.argument.CommandArgument;

@RequiredArgsConstructor
public class IntegerArgument implements CommandArgument<Integer> {

    @Getter
    private final String name;

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

}
