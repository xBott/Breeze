package me.bottdev.breezeapi.di;

import me.bottdev.breezeapi.di.index.ComponentIndex;
import me.bottdev.breezeapi.di.index.SupplierIndex;

import java.util.Optional;

public interface BreezeContext {

    void load();

    void load(ClassLoader classLoader);

    void loadSuppliersFromClassLoader(ClassLoader classLoader);

    void loadSuppliersFromIndex(SupplierIndex index, ClassLoader classLoader);

    void loadComponentsFromClassLoader(ClassLoader classLoader);

    void loadComponentsFromIndex(ComponentIndex index, ClassLoader classLoader);

    void addSupplier(Object object);

    <T> Optional<T> get(Class<T> clazz, String key);

    <T> T injectConstructor(Class<T> clazz);

    void injectFields(Object object);

}
