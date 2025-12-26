package me.bottdev.breezeapi.config.validation.rules;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.config.Configuration;
import me.bottdev.breezeapi.config.validation.*;
import me.bottdev.breezeapi.config.validation.annotations.StructureIgnore;
import me.bottdev.breezeapi.serialization.ObjectNode;

import java.lang.reflect.Field;
import java.util.Optional;

@RequiredArgsConstructor
public class StructureRule implements ValidationRule {

    private final Class<? extends Configuration> clazz;

    @Override
    public void validate(ValidationContext context, ValidationNode validationNode) {

        ObjectNode node = context.getNode();
        String path = context.getPath();

        for (Field field : clazz.getDeclaredFields()) {

            String fieldName = field.getName();

            if (field.isAnnotationPresent(StructureIgnore.class)) {
                continue;
            }

            String fieldPath = path.isEmpty() ? fieldName : path + "." + fieldName;

            if (!node.hasChild(fieldName)) {
                validationNode.setStatus(ValidationStatus.ERROR);
                validationNode.addError(new ValidationError("Missing required field %s".formatted(fieldPath)));
                continue;
            }

            Optional<ObjectNode> childOptional = node.getChild(fieldName);
            if (childOptional.isEmpty()) {
                validationNode.setStatus(ValidationStatus.ERROR);
                validationNode.addError(new ValidationError("Required field %s is null".formatted(fieldPath)));
                continue;
            }
            ObjectNode child = childOptional.get();

            if (Configuration.class.isAssignableFrom(field.getType())) {
                ValidationContext childContext = new ValidationContext(child, fieldPath, fieldName);
                StructureRule childRule = new StructureRule((Class<? extends Configuration>) field.getType());
                childRule.validate(childContext, validationNode);
            }

        }

    }

}
