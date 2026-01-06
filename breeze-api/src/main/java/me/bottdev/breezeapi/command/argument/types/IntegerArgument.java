package me.bottdev.breezeapi.command.argument.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.argument.CommandArgument;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class IntegerArgument implements CommandArgument<Integer> {

    private final String name;
    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

}
