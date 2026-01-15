package me.bottdev.breezecore.di;

import lombok.Getter;
import me.bottdev.breezeapi.commons.PrimitiveWrappers;
import me.bottdev.breezeapi.di.Bean;
import me.bottdev.breezeapi.di.BindingKey;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.di.annotations.Qualifier;
import me.bottdev.breezeapi.di.exceptions.ContextInjectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.*;

@Getter
public class LocalContext implements BreezeContext {

    private final Map<BindingKey<?>, Bean<?>> bindings = new HashMap<>();

    @Override
    public <T> T get(BindingKey<T> key) {
        Bean<?> bean = bindings.get(key);
        if (bean == null) return null;
        Object value = bean.get();
        return key.getType().cast(value);
    }

    @Override
    public <T> T get(Class<T> type) {
        BindingKey<T> key = BindingKey.of(type, null);
        return get(key);
    }

    @Override
    public <T> T get(Class<T> type, String qualifier) {
        BindingKey<T> key = BindingKey.of(type, qualifier);
        return get(key);
    }

    @Override
    public <T> Optional<T> find(BindingKey<T> key) {
        T value = get(key);
        return Optional.ofNullable(value);
    }

    @Override
    public <T> Optional<T> find(Class<T> type) {
        T value = get(type);
        return Optional.ofNullable(value);
    }

    @Override
    public <T> Optional<T> find(Class<T> type, String qualifier) {
        T value = get(type, qualifier);
        return Optional.ofNullable(value);
    }

    @Override
    public <T> T createObject(Class<T> implementation) throws ContextInjectionException {

        Constructor<?>[] allConstructors = implementation.getDeclaredConstructors();

        List<Constructor<?>> injectConstructors = Arrays.stream(allConstructors)
                .filter(c -> c.isAnnotationPresent(Inject.class))
                .toList();

        if (injectConstructors.size() > 1) {
            throw new ContextInjectionException("Multiple @Inject constructors found in " + implementation.getName());
        }

        T injectedObject;

        if (injectConstructors.isEmpty()) {

            injectedObject = injectEmptyConstructor(implementation, allConstructors);

        } else {

            Constructor<?> injectConstructor = injectConstructors.getFirst();
            injectedObject = injectNonEmptyConstructor(implementation, injectConstructor);

        }

        return injectedObject;

    }

    @SuppressWarnings("unchecked")
    private <T> T injectEmptyConstructor(Class<?> clazz, Constructor<?>[] constructors)
            throws ContextInjectionException
    {
        List<Constructor<?>> noArgConstructors = Arrays.stream(constructors)
                .filter(c -> c.getParameterCount() == 0)
                .toList();

        if (noArgConstructors.isEmpty()) {
            throw new ContextInjectionException("No empty constructors found in " + clazz.getName());
        }

        try {
            if (noArgConstructors.size() == 1) {
                Constructor<?> noArgConstructor = noArgConstructors.getFirst();
                return (T) clazz.cast(noArgConstructor.newInstance());

            }  else {
                throw new ContextInjectionException("Failed to inject constructor " + clazz.getName() + ". has multiple empty constructors.");

            }

        } catch (Exception ex) {
            throw new ContextInjectionException("Failed to inject empty inject constructor for " + clazz.getName(), ex);

        }

    }

    private boolean isAssignable(Class<?> targetType, Object value) {

        if (targetType.isInstance(value)) {
            return true;
        }

        Class<?> valueClass = value.getClass();

        if (Proxy.isProxyClass(valueClass)) {
            for (Class<?> iface : valueClass.getInterfaces()) {
                if (iface.getName().equals(targetType.getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    private <T> T injectNonEmptyConstructor(Class<?> clazz, Constructor<?> constructor)
            throws ContextInjectionException
    {
        try {

            if (constructor == null) {
                return (T) clazz.getDeclaredConstructor().newInstance();
            }

            constructor.setAccessible(true);

            Parameter[] parameters = constructor.getParameters();
            Object[] suppliedParameters = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];

                String parameterName = parameter.getName();
                Class<?> type = parameter.getType();
                type = PrimitiveWrappers.get(type);

                String name = parameter.isAnnotationPresent(Qualifier.class) ?
                        parameter.getAnnotation(Qualifier.class).value() : null;

                Object object = get(type, name);

                if (object == null) {
                    throw new ContextInjectionException("No bean found for parameter \"" + parameterName + "\"");

                }

                if (!isAssignable(type, object)) {
                    throw new ContextInjectionException("Found bean is not assignable to parameter \"" + parameterName + "\"");

                }

                suppliedParameters[i] = object;
            }

            return (T) clazz.cast(constructor.newInstance(suppliedParameters));

        } catch (Exception ex) {
            throw new ContextInjectionException("Failed to inject dependencies of constructor for " + clazz.getName(), ex);

        }
    }

}
