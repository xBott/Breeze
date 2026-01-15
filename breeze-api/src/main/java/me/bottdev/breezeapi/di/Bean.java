package me.bottdev.breezeapi.di;

public interface Bean<T> {

    BeanScope getScope();

    T get();

}
