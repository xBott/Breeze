package me.bottdev.breezeapi.di.beans;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.di.Bean;
import me.bottdev.breezeapi.di.BeanScope;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class SingletonBean<T> implements Bean<T> {

    private final Supplier<T> supplier;

    private T value = null;

    @Override
    public BeanScope getScope() {
        return BeanScope.SINGLETON;
    }

    @Override
    public T get() {
        if (value == null) {
            value = supplier.get();
        }
        return value;
    }

}
