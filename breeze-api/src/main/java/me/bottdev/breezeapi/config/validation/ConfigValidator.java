package me.bottdev.breezeapi.config.validation;

import me.bottdev.breezeapi.serialization.ObjectNode;

public interface ConfigValidator {

    ValidationResult validate(ObjectNode root);

}
