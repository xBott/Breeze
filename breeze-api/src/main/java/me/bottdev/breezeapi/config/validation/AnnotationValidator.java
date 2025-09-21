package me.bottdev.breezeapi.config.validation;

import me.bottdev.breezeapi.serialization.ObjectNode;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

@FunctionalInterface
public interface AnnotationValidator<T extends Annotation> {
    FieldStatus validate(T annotation, ObjectNode node, Field field);
}
