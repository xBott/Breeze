package me.bottdev.breezeapi.command.argument.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.bottdev.breezeapi.command.argument.CommandArgument;
import me.bottdev.breezeapi.command.argument.Suggestable;
import me.bottdev.breezeapi.command.argument.suggestion.SuggestionProvider;

import java.util.Optional;

@RequiredArgsConstructor
public class StringArgument implements CommandArgument<String>, Suggestable {

    @Getter
    private final String name;
    @Setter
    private SuggestionProvider suggestionProvider = null;

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public Optional<SuggestionProvider> getSuggestionProvider() {
        return Optional.ofNullable(suggestionProvider);
    }

}
