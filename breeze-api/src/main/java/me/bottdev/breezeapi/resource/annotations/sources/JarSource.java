package me.bottdev.breezeapi.resource.annotations.sources;

import me.bottdev.breezeapi.resource.annotations.ResourceSourceDef;
import me.bottdev.breezeapi.resource.source.SourceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ResourceSourceDef(type = SourceType.JAR, defaultPriority = 5)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JarSource {
    int priority() default Integer.MIN_VALUE;
    String path() default "";
}
