package me.bottdev.breezeapi.command.scheme.resolvers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.CommandExecutionContext;
import me.bottdev.breezeapi.command.scheme.SchemeResolver;

@Getter
@RequiredArgsConstructor
public class ArgumentSchemeResolver implements SchemeResolver {

    private final String name;
    private final Class<?> type;
    private final boolean isRequired;

    @Override
    public Object resolve(CommandExecutionContext context) {
        return context.getArgument(name);
    }

}
