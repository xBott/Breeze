package me.bottdev.breezeapi.command.annotations;

import me.bottdev.breezeapi.command.argument.suggestion.SuggestionFactory;
import me.bottdev.breezeapi.command.argument.suggestion.types.EmptySuggestionFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Argument {
    String name() default "";
    boolean required() default true;
    Class<? extends SuggestionFactory> suggest() default EmptySuggestionFactory.class;

}
