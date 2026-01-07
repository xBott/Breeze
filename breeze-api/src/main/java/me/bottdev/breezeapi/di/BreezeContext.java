package me.bottdev.breezeapi.di;

import me.bottdev.breezeapi.commons.reflection.ReflectionCommons;
import me.bottdev.breezeapi.log.BreezeLogger;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BreezeContext {

    BreezeLogger getLogger();

    Map<String, ObjectSupplier> getSuppliers();

    List<ConstructHook> getConstructHooks();

    void addSupplier(Object object);

    void addObjectSupplier(String key, ObjectSupplier supplier);

    <T> Optional<T> get(Class<T> clazz, String key);

    default <T> Optional<T> get(Class<T> clazz) {
        String key = ReflectionCommons.asFieldName(clazz);
        return get(clazz, key);
    }

    <T> Optional<T> injectConstructor(Class<T> clazz);

    default <T> Optional<T> injectConstructorAndApplyHooks(Class<T> clazz) {
        Optional<T> optional = injectConstructor(clazz);
        optional.ifPresent(this::applyConstructHooks);
        return optional;
    }

    void registerConstructHook(ConstructHook constructHook);

    default void applyConstructHooks(Object object) {
        getConstructHooks().forEach(constructHook -> constructHook.accept(object));
    }

    void injectFields(Object object);

    default <T> boolean createComponent(String name, SupplyType type, Class<T> clazz) {

        Optional<?> optionalInjectedObject = injectConstructor(clazz);
        if (optionalInjectedObject.isEmpty()) return false;

        ObjectSupplier supplier = SupplierFactory.create(
                type,
                () -> injectConstructorAndApplyHooks(clazz).orElseThrow()
        );

        addObjectSupplier(name, supplier);

        return true;
    }

}
