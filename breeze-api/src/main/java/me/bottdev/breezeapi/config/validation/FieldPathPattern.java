package me.bottdev.breezeapi.config.validation;

@FunctionalInterface
public interface FieldPathPattern {
    boolean matches(String path);

}
