package me.bottdev.breezeapi.modules.annotations;

public @interface ModuleDependency {
    String name() default "";
}
