package me.bottdev.breezeapi.resource.annotations;

import me.bottdev.breezeapi.resource.source.SourceType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface ResourceSourceDef {
    SourceType type();
    int defaultPriority();
}
