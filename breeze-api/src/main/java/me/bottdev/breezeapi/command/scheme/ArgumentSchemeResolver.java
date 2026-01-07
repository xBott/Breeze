package me.bottdev.breezeapi.command.scheme;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.CommandExecutionContext;

@Getter
@RequiredArgsConstructor
public class ArgumentSchemeResolver implements SchemeResolver {

    private final String name;

    @Override
    public Object resolve(CommandExecutionContext context) {
        return context.getArgument(name);
    }

}
