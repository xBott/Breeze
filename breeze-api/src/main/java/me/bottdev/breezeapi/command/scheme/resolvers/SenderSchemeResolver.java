package me.bottdev.breezeapi.command.scheme.resolvers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.command.CommandExecutionContext;
import me.bottdev.breezeapi.command.scheme.SchemeResolver;

@Getter
@RequiredArgsConstructor
public class SenderSchemeResolver implements SchemeResolver {

    @Override
    public boolean isRequired() {
        return false;
    }

    @Override
    public Object resolve(CommandExecutionContext context) {
        return context.getSender();
    }

}
