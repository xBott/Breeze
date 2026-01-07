package me.bottdev.breezeapi.command.argument;

import me.bottdev.breezeapi.command.argument.suggestion.SuggestionProvider;

import java.util.Optional;

public interface Suggestable {

    void setSuggestionProvider(SuggestionProvider suggestionProvider);

    Optional<SuggestionProvider> getSuggestionProvider();

}
