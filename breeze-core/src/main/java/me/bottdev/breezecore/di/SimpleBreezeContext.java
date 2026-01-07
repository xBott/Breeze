package me.bottdev.breezecore.di;

import lombok.Getter;
import me.bottdev.breezeapi.di.*;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.di.annotations.Named;
import me.bottdev.breezeapi.di.annotations.Supply;
import me.bottdev.breezeapi.log.BreezeLogger;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;

public class SimpleBreezeContext implements BreezeContext {

    private static final Map<Class<?>, Class<?>> primitiveWrappers = Map.of(
            int.class, Integer.class,
            long.class, Long.class,
            boolean.class, Boolean.class,
            double.class, Double.class,
            float.class, Float.class,
            short.class, Short.class,
            byte.class, Byte.class,
            char.class, Character.class
    );

    @Getter
    protected final BreezeLogger logger;
    @Getter
    protected final Map<String, ObjectSupplier> suppliers = new HashMap<>();
    @Getter
    private final List<ConstructHook> constructHooks = new ArrayList<>();

    public SimpleBreezeContext(BreezeLogger logger) {
        this.logger = logger;
    }

    @Override
    public void registerConstructHook(ConstructHook constructHook) {
        constructHooks.add(constructHook);
    }

    @Override
    public void addSupplier(Object object) {
        Class<?> clazz = object.getClass();
        for (Method method : clazz.getDeclaredMethods()) {

            if (!method.isAnnotationPresent(Supply.class)) continue;

            Supply supply = method.getAnnotation(Supply.class);
            SupplyType type = supply.type();
            String key = method.getName();

            method.setAccessible(true);

            Supplier<?> supplier = () -> {
                try {
                    return method.invoke(object);
                } catch (Exception ex) {
                    throw new RuntimeException("Failed to invoke supplier method", ex);
                }
            };

            ObjectSupplier objectSupplier = SupplierFactory.create(type, supplier);
            addObjectSupplier(key, objectSupplier);

        }
    }

    @Override
    public void addObjectSupplier(String key, ObjectSupplier supplier) {
        suppliers.put(key, supplier);
    }

    @Override
    public <T> Optional<T> get(Class<T> clazz, String key) {

        ObjectSupplier supplier = suppliers.get(key);
        if (supplier == null) return Optional.empty();

        Object supplied = supplier.supply();
        if (!clazz.isInstance(supplied)) return Optional.empty();

        return Optional.of(clazz.cast(supplied));
    }

    private boolean isAssignable(Class<?> targetType, Object value) {

        if (targetType.isInstance(value)) {
            return true;
        }

        if (targetType.isPrimitive()) {
            Class<?> wrapper = primitiveWrappers.get(targetType);
            if (wrapper != null && wrapper.isInstance(value)) {
                return true;
            }
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


    @Override
    public <T> Optional<T> injectConstructor(Class<T> clazz) {

        Constructor<?>[] allConstructors = clazz.getDeclaredConstructors();

        List<Constructor<?>> injectConstructors = Arrays.stream(allConstructors)
                .filter(c -> c.isAnnotationPresent(Inject.class))
                .toList();

        if (injectConstructors.size() > 1) {
            throw new RuntimeException("Multiple @Inject constructors found in " + clazz.getName());
        }

        Optional<T> injectedObject;

        if (injectConstructors.isEmpty()) {

            injectedObject = injectEmptyConstructor(clazz, allConstructors);

        } else {

            Constructor<?> injectConstructor = injectConstructors.getFirst();
            injectedObject = injectNonEmptyConstructor(clazz, injectConstructor);

        }

        return injectedObject;

    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> injectEmptyConstructor(Class<?> clazz, Constructor<?>[] constructors) {
        List<Constructor<?>> noArgConstructors = Arrays.stream(constructors)
                .filter(c -> c.getParameterCount() == 0)
                .toList();

        if (noArgConstructors.isEmpty()) {
            logger.warn("No empty constructors found in " + clazz.getName());
            return Optional.empty();
        }

        try {
            if (noArgConstructors.size() == 1) {
                Constructor<?> noArgConstructor = noArgConstructors.getFirst();
                return (Optional<T>) Optional.of(clazz.cast(noArgConstructor.newInstance()));
            }  else {
                logger.warn("Failed to inject constructor. {} has multiple empty constructors.", clazz.getName());
                return Optional.empty();
            }

        } catch (Exception ex) {
            logger.error("Failed to inject empty constructor for " + clazz.getName(), ex);
            return Optional.empty();
        }

    }

    @SuppressWarnings("unchecked")
    private <T> Optional<T> injectNonEmptyConstructor(Class<?> clazz, Constructor<?> constructor) {
        try {

            if (constructor == null) {
                return (Optional<T>) Optional.of(clazz.getDeclaredConstructor().newInstance());
            }

            constructor.setAccessible(true);

            Parameter[] parameters = constructor.getParameters();
            Object[] suppliedParameters = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Class<?> type = parameter.getType();
                type = primitiveWrappers.getOrDefault(type, type);

                String name = parameter.isAnnotationPresent(Named.class) ?
                        parameter.getAnnotation(Named.class).value() : parameter.getName();

                Object supplied = get(type, name).orElse(null);

                if (supplied == null) {
                    logger.warn("No supplier found for key: {} with type: {}", name, type);
                    return Optional.empty();
                }

                if (!isAssignable(type, supplied)) {
                    logger.warn("Supplier for key {} is not assignable to {}",  name, type);
                    return Optional.empty();
                }

                suppliedParameters[i] = supplied;
            }

            return (Optional<T>) Optional.of(clazz.cast(constructor.newInstance(suppliedParameters)));

        } catch (Exception ex) {
            logger.error("Failed to inject dependencies of constructor for " + clazz.getName(), ex);
            return Optional.empty();
        }
    }

    @Override
    public void injectFields(Object object) {
        Class<?> clazz = object.getClass();

        for (Field field : clazz.getDeclaredFields()) {

            if (!field.isAnnotationPresent(Inject.class)) continue;

            String key = field.getName();
            Class<?> type = field.getType();
            type = primitiveWrappers.getOrDefault(type, type);

            Optional<?> valueOptional = get(type, key);

            if (valueOptional.isEmpty()) continue;

            Object value = valueOptional.get();

            try {
                field.setAccessible(true);
                field.set(object, value);
            } catch (Exception ex) {
                throw new RuntimeException("Failed to inject dependency into field " + key + " of " + object, ex);
            }

        }
    }

}
