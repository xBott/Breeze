package me.bottdev.breezeapi.command.argument.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.argument.CommandArgument;

@RequiredArgsConstructor
public class FloatArgument implements CommandArgument<Float> {

    @Getter
    private final String name;

    @Override
    public Class<Float> getType() {
        return Float.class;
    }

}
