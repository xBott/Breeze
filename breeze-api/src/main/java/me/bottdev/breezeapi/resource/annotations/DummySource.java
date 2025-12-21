package me.bottdev.breezeapi.resource.annotations;

import me.bottdev.breezeapi.resource.source.SourceType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ResourceSourceDef(type = SourceType.DUMMY, defaultPriority = 2)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DummySource {
    int priority() default Integer.MIN_VALUE;
    String value() default "";
}
