package me.bottdev.breezeapi.command.argument.suggestion;

import java.util.List;

@FunctionalInterface
public interface SuggestionProvider {
    List<String> provide();

}
