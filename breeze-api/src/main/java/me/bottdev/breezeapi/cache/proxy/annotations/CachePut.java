package me.bottdev.breezeapi.cache.proxy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CachePut {
    String group() default "default";
    String key() default "default";
    int size() default 20;
    int ttl() default 1000;

}
