package me.bottdev.breezeapi.command.scheme;

import me.bottdev.breezeapi.command.CommandExecutionContext;

@FunctionalInterface
public interface SchemeResolver {
    Object resolve(CommandExecutionContext context);
}
