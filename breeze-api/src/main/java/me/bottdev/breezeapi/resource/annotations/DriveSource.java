package me.bottdev.breezeapi.resource.annotations;

import me.bottdev.breezeapi.resource.source.SourceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ResourceSourceDef(type = SourceType.DRIVE, defaultPriority = 100)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DriveSource {
    int priority() default Integer.MIN_VALUE;
    String path() default "";
    boolean createIfAbsent() default true;
    String defaultValue() default "";
}
