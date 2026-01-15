package me.bottdev.breezeapi.di;

import me.bottdev.breezeapi.di.beans.PrototypeBean;
import me.bottdev.breezeapi.di.beans.SingletonBean;

import java.util.function.Supplier;

public class BeanFactory {

    public static <T> Bean<T> create(Supplier<T> supplier, BeanScope type) {
        if (type == BeanScope.PROTOTYPE) {
            return new PrototypeBean<>(supplier);
        }
        return new SingletonBean<>(supplier);
    }

}
