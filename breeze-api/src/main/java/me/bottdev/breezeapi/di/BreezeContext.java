package me.bottdev.breezeapi.di;

import me.bottdev.breezeapi.di.exceptions.ContextInjectionException;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public interface BreezeContext {

    Map<BindingKey<?>, Bean<?>> getBindings();

    default void registerUnchecked(
            BindingKey<?> key,
            Supplier<?> supplier,
            BeanScope type
    ) {

        Bean<?> bean = BeanFactory.create(supplier, type);
        getBindings().put(key, bean);

    }

    default <T> void registerTypeSafe(
            BindingKey<T> key,
            Supplier<T> supplier,
            BeanScope type
    ) {
        registerUnchecked(key, supplier, type);
    }

    default <T> void registerImplementation(
            BindingKey<T> key,
            Class<? extends T> implementation,
            BeanScope type
    ) throws ContextInjectionException {

        try {

            T instance = createObjectWithHooks(implementation);
            registerTypeSafe(key, () -> instance, type);

        } catch (ContextInjectionException ex) {
            throw new ContextInjectionException("Failed to register bean " + implementation.getName(), ex);

        }

    }

    default <T> BindingBuilder<T> bind(Class<T> type) {
        return new BindingBuilder<>(this, type);
    }

    <T> T get(BindingKey<T> key);

    <T> T get(Class<T> type);

    <T> T get(Class<T> type, String qualifier);

    <T> Optional<T> find(BindingKey<T> key);

    <T> Optional<T> find(Class<T> type);

    <T> Optional<T> find(Class<T> type, String qualifier);

    default <T> T createObjectWithHooks(Class<T> implementation) throws ContextInjectionException {
        T instance = createObject(implementation);
        if (instance instanceof PostConstructHook postConstructHook) {
            postConstructHook.onPostConstruct();
        }
        return instance;
    }

    <T> T createObject(Class<T> implementation) throws ContextInjectionException;

}
