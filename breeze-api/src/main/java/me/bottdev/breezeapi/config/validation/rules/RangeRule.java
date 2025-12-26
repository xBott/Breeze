package me.bottdev.breezeapi.config.validation.rules;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.config.validation.*;
import me.bottdev.breezeapi.serialization.ObjectNode;

@RequiredArgsConstructor
public class RangeRule implements ValidationRule {

    private final double min;
    private final double max;

    @Override
    public void validate(ValidationContext context, ValidationNode validationNode) {

        ObjectNode node = context.getNode();
        Object value = node.getValue();

        if (value == null) {
            validationNode.setStatus(ValidationStatus.ERROR);
            validationNode.addError(ValidationError.nullError());
            return;
        }
        if (!(value instanceof Number)) {
            validationNode.setStatus(ValidationStatus.ERROR);
            validationNode.addError(new ValidationError("Value of field is not a number"));
            return;
        }

        double doubleValue = ((Number) value).doubleValue();
        if (doubleValue < min || doubleValue > max) {
            validationNode.setStatus(ValidationStatus.ERROR);
            validationNode.addError(new ValidationError(
                    "Value %s is out of range [%s, %s]"
                    .formatted(doubleValue, min, max)
            ));
        }

    }

}
