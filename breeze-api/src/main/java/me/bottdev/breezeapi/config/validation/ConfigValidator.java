package me.bottdev.breezeapi.config.validation;

import me.bottdev.breezeapi.config.Configuration;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleLogger;
import me.bottdev.breezeapi.serialization.ObjectNode;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Optional;

public class ConfigValidator {

    private final BreezeLogger logger = new SimpleLogger("ConfigValidator");
    private final ValidatorRegistry registry = ValidatorRegistry.createDefault();

    public <T extends Configuration> ConfigStatus validateTree(ObjectNode node, Class<T> clazz) {

        ConfigStatus configStatus = ConfigStatus.SUCCESS;

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Optional<ObjectNode> fieldNodeOptional = node.getChild(field.getName());

            if (fieldNodeOptional.isEmpty()) {
                logger.info("Field " + field.getName() + " has no child.");
                configStatus = ConfigStatus.ERROR;
                break;
            }

            ObjectNode fieldNode = fieldNodeOptional.get();

            configStatus = validateFieldInFile(fieldNode, field);
            if (configStatus == ConfigStatus.ERROR) break;
        }

        String message = configStatus.getMessage();
        logger.info(message);

        return configStatus;
    }

    private ConfigStatus validateFieldInFile(ObjectNode node, Field field) {

        String name = field.getName();
        Annotation[] annotations = field.getAnnotations();

        ConfigStatus finalStatus = ConfigStatus.SUCCESS;

        for (Annotation annotation : annotations) {

            Optional<ValidationHandler<?>> validatorOptional = registry.getValidator(annotation.annotationType());

            if (validatorOptional.isEmpty()) continue;

            ValidationHandler<?> validator = validatorOptional.get();
            @SuppressWarnings("unchecked")
            ValidationHandler<Annotation> typedValidator = (ValidationHandler<Annotation>) validator;
            FieldStatus status = typedValidator.validate(annotation, node, field);

            String message = status.getFormattedMessage(name);
            logger.info(message);

            if (status != FieldStatus.SUCCESS) {
                finalStatus = ConfigStatus.ERROR;
            }

        }
        return finalStatus;
    }

}
