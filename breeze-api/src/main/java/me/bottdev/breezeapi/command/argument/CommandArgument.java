package me.bottdev.breezeapi.command.argument;

public interface CommandArgument<T> {

    String getName();

    Class<T> getType();

}
