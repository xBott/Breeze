package me.bottdev.breezeapi.command.scheme;

import me.bottdev.breezeapi.command.CommandExecutionContext;

public interface SchemeResolver {

    boolean isRequired();

    Object resolve(CommandExecutionContext context);

}
