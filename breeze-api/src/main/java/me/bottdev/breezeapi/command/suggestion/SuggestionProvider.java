package me.bottdev.breezeapi.command.suggestion;

import java.util.List;

@FunctionalInterface
public interface SuggestionProvider {
    List<String> provide();

}
