package me.bottdev.breezeapi.command.annotations;

import me.bottdev.breezeapi.command.suggestion.SuggestionProviderFactory;
import me.bottdev.breezeapi.command.suggestion.types.EmptySuggestionFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Suggest {
    Class<? extends SuggestionProviderFactory> value() default EmptySuggestionFactory.class;

}
