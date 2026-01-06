package me.bottdev.breezeapi.command.suggestion.types;

import me.bottdev.breezeapi.command.suggestion.SuggestionProvider;
import me.bottdev.breezeapi.command.suggestion.SuggestionProviderFactory;

import java.util.List;

public class EmptySuggestionFactory implements SuggestionProviderFactory {

    @Override
    public SuggestionProvider create() {
        return List::of;
    }

}
