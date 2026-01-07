package me.bottdev.breezeapi.command.argument.suggestion.types;

import me.bottdev.breezeapi.command.argument.suggestion.SuggestionProvider;
import me.bottdev.breezeapi.command.argument.suggestion.SuggestionFactory;

import java.util.List;

public class EmptySuggestionFactory implements SuggestionFactory {

    @Override
    public SuggestionProvider create() {
        return List::of;
    }

}
