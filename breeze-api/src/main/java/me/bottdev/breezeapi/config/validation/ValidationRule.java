package me.bottdev.breezeapi.config.validation;

@FunctionalInterface
public interface ValidationRule {
    void validate(ValidationContext context, ValidationNode validationNode);
}
