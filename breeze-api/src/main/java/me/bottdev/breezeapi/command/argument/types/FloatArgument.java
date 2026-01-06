package me.bottdev.breezeapi.command.argument.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.argument.CommandArgument;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class FloatArgument implements CommandArgument<Float> {

    private final String name;
    private float min = Integer.MIN_VALUE;
    private float max = Integer.MAX_VALUE;

    @Override
    public Class<Float> getType() {
        return Float.class;
    }

}
