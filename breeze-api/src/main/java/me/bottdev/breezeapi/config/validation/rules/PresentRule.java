package me.bottdev.breezeapi.config.validation.rules;

import me.bottdev.breezeapi.config.validation.*;
import me.bottdev.breezeapi.serialization.ObjectNode;

public class PresentRule implements ValidationRule {

    @Override
    public void validate(ValidationContext context, ValidationNode validationNode) {

        ObjectNode node = context.getNode();
        Object value = node.getValue();

        if (value == null) {
            validationNode.setStatus(ValidationStatus.ERROR);
            validationNode.addError(ValidationError.nullError());
        }

    }

}
