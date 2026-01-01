package me.bottdev.breezeapi.command.nodes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.bottdev.breezeapi.command.CommandNode;
import me.bottdev.breezeapi.command.argument.CommandArgument;
import me.bottdev.breezeapi.command.suggestion.SuggestionProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class CommandArgumentNode implements CommandNode {

    @Getter
    private final CommandArgument<?> argument;

    @Setter
    private SuggestionProvider suggestionProvider = null;
    @Getter
    private final Map<String, CommandNode> children = new HashMap<>();

    @Override
    public String getValue() {
        return argument.getName();
    }

    @Override
    public String getDisplayName() {
        return "<" + argument.getName() + "> (argument:" + argument.getType().getSimpleName() + ")";
    }

    public Optional<SuggestionProvider> getSuggestionProvider() {
        return Optional.ofNullable(suggestionProvider);
    }

}
