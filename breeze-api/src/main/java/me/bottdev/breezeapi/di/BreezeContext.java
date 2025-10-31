package me.bottdev.breezeapi.di;

import me.bottdev.breezeapi.log.BreezeLogger;

import java.util.Optional;

public interface BreezeContext {

    BreezeLogger getLogger();

    ContextReader getContextReader();

    void addSupplier(Object object);

    void addObjectSupplier(String key, ObjectSupplier supplier);

    <T> Optional<T> get(Class<T> clazz, String key);

    <T> T injectConstructor(Class<T> clazz);

    void injectFields(Object object);

}
