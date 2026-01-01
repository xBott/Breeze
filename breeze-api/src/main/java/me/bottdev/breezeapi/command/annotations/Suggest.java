package me.bottdev.breezeapi.command.annotations;

import me.bottdev.breezeapi.command.suggestion.SuggestionProviderFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Suggest {
    Class<? extends SuggestionProviderFactory> value();
}
