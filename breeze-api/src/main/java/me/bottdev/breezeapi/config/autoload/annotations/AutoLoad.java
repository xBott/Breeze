package me.bottdev.breezeapi.config.autoload.annotations;

import me.bottdev.breezeapi.config.autoload.AutoLoadSerializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoLoad {
    AutoLoadSerializer serializer() default AutoLoadSerializer.JSON;
    String path() default "{module}/{name}.{extension}";
}
