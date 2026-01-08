package me.bottdev.breezepaper;

import me.bottdev.breezeapi.command.argument.suggestion.SuggestionFactory;
import me.bottdev.breezeapi.command.argument.suggestion.SuggestionProvider;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.modules.ModuleManager;

import java.util.stream.Collectors;

public class ModuleSuggestionFactory implements SuggestionFactory {

    private final ModuleManager moduleManager;

    @Inject
    public ModuleSuggestionFactory(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    @Override
    public SuggestionProvider create() {
        return () -> moduleManager.getModules().stream()
                .map(module -> module.getDescriptor().getName())
                .collect(Collectors.toList());
    }

}
