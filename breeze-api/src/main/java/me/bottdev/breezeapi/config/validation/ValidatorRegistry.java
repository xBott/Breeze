package me.bottdev.breezeapi.config.validation;

import me.bottdev.breezeapi.config.validation.annotations.Ignore;
import me.bottdev.breezeapi.config.validation.annotations.NotEmpty;
import me.bottdev.breezeapi.config.validation.annotations.Range;
import org.apache.commons.lang3.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ValidatorRegistry {

    public static ValidatorRegistry createDefault() {
        ValidatorRegistry registry = new ValidatorRegistry();

        registry.register(Ignore.class, (_, _, _) -> FieldStatus.SUCCESS);

        registry.register(NotEmpty.class, (_, node, field) -> {

            Class<?> fieldType = field.getType();

            Object value = node.getValue();
            if (value == null) return FieldStatus.EMPTY;

            Class<?> valueType = value.getClass();

            if (fieldType.isPrimitive()) {
                fieldType = ClassUtils.primitiveToWrapper(fieldType);
            }

            if (!fieldType.isAssignableFrom(valueType)) return FieldStatus.INCORRECT_TYPE;

            return FieldStatus.SUCCESS;

        });

        registry.register(Range.class, (range, node, _) -> {

            double min = range.min();
            double max = range.max();

            Object value = node.getValue();
            if (value == null) return FieldStatus.EMPTY;
            if (!(value instanceof Number number)) return FieldStatus.INCORRECT_TYPE;

            double numValue = number.doubleValue();

            if (numValue < min || numValue > max) {
                return FieldStatus.NOT_IN_RANGE;
            }

            return FieldStatus.SUCCESS;

        }) ;

        return registry;
    }

    private final Map<Class<? extends Annotation>, AnnotationValidator<?>> validators = new HashMap<>();

    public boolean isRegistered(Class<? extends Annotation> annotationClass) {
        return validators.containsKey(annotationClass);
    }

    public <T extends Annotation> void register(Class<T> annotationClass, AnnotationValidator<T> validator) {
        if (isRegistered(annotationClass)) return;
        validators.put(annotationClass, validator);
    }

    public Optional<AnnotationValidator<?>> getValidator(Class<? extends Annotation> annotationClass) {
        return Optional.ofNullable(validators.get(annotationClass));
    }
}
