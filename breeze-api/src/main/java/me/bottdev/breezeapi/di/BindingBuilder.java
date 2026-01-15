package me.bottdev.breezeapi.di;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.di.exceptions.ContextInjectionException;

import java.util.function.Supplier;

@RequiredArgsConstructor
public final class BindingBuilder<T> {

    private final BreezeContext context;
    private final Class<T> clazz;

    private BeanScope scope = BeanScope.SINGLETON;
    private String qualifier;
    private Runnable onFailure;

    public BindingBuilder<T> scope(BeanScope type) {
        scope = type;
        return this;
    }

    public BindingBuilder<T> qualified(String qualifier) {
        this.qualifier = qualifier;
        return this;
    }

    public BindingBuilder<T> failure(Runnable onFailure) {
        this.onFailure = onFailure;
        return this;
    }

    public void to(Class<? extends T> implementation) {
        BindingKey<T> key = BindingKey.of(clazz, qualifier);

        try {
            context.registerImplementation(key, implementation, scope);

        } catch (ContextInjectionException ex) {
            onFailure.run();
        }

    }

    public void self() {
        BindingKey<T> key = BindingKey.of(clazz, qualifier);

        try {
            context.registerImplementation(key, clazz, scope);

        } catch (ContextInjectionException ex) {
            onFailure.run();
        }

    }

    public void instance(T instance) {
        BindingKey<T> key = BindingKey.of(clazz, qualifier);
        context.registerTypeSafe(key, () -> instance, scope);
    }

    public void instance(Supplier<T> supplier) {
        BindingKey<T> key = BindingKey.of(clazz, qualifier);
        context.registerTypeSafe(key, supplier, scope);
    }

    public void unchecked(Supplier<?> supplier) {
        BindingKey<T> key = BindingKey.of(clazz, qualifier);
        context.registerUnchecked(key, supplier, scope);
    }

}

