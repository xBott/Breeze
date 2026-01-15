package me.bottdev.breezeapi.di.annotations;

import me.bottdev.breezeapi.di.BeanScope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Build {
    BeanScope type() default BeanScope.SINGLETON;
}
