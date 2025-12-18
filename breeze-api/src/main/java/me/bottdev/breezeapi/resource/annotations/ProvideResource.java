package me.bottdev.breezeapi.resource.annotations;

import me.bottdev.breezeapi.resource.Resource;
import me.bottdev.breezeapi.resource.fallback.Fallback;
import me.bottdev.breezeapi.resource.provide.Source;
import me.bottdev.breezeapi.resource.types.file.SingleFileResource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ProvideResource {
    Source source() default Source.DRIVE;
    Class<? extends Resource> type() default SingleFileResource.class;
    boolean isTree() default false;
    Fallback fallback() default Fallback.NONE;

}
