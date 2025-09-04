package me.bottdev.breezeapi.di.annotations;

import me.bottdev.breezeapi.di.SupplyType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface Component {
    SupplyType type() default SupplyType.SINGLETON;
}
