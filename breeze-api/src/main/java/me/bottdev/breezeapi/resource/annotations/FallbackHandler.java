package me.bottdev.breezeapi.resource.annotations;

import me.bottdev.breezeapi.resource.fallback.ResourceFallbackHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FallbackHandler {
    Class<? extends ResourceFallbackHandler<?>> value();

}
