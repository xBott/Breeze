package me.bottdev.breezeapi.di.beans;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.di.Bean;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class PrototypeBean<T> implements Bean<T> {

    private final Supplier<T> supplier;

    @Override
    public T get() {
        return supplier.get();
    }

}
