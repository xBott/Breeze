package me.bottdev.breezecore.di;

import lombok.Getter;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.ObjectSupplier;
import me.bottdev.breezeapi.di.SupplierFactory;
import me.bottdev.breezeapi.di.SupplyType;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.di.annotations.Named;
import me.bottdev.breezeapi.di.annotations.Supply;
import me.bottdev.breezeapi.di.index.ComponentIndex;
import me.bottdev.breezeapi.di.index.SupplierIndex;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
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
    private final Map<String, ObjectSupplier> suppliers = new HashMap<>();

    @Override
    public void load() {
        ClassLoader classLoader = getClass().getClassLoader();
        load(classLoader);
    }

    @Override
    public void load(ClassLoader classLoader) {
        loadSuppliersFromClassLoader(classLoader);
        loadComponentsFromClassLoader(classLoader);
    }

    @Override
    public void loadSuppliersFromClassLoader(ClassLoader classLoader) {
        try (InputStream in = classLoader.getResourceAsStream("META-INF/breeze-supplier-index.json")) {

            if (in == null) return;
            String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Optional<SupplierIndex> optionalIndex = SupplierIndex.fromJson(content);
            optionalIndex.ifPresent(index -> loadSuppliersFromIndex(index, classLoader));

        } catch (Exception ex) {
            throw new RuntimeException("Failed to load suppliers from index", ex);
        }
    }

    @Override
    public void loadSuppliersFromIndex(SupplierIndex index, ClassLoader classLoader) {
        index.getPaths().forEach(path -> {

            try {
                Class<?> clazz = classLoader.loadClass(path);
                addSupplier(clazz.getDeclaredConstructor().newInstance());
            } catch (Exception ex) {
                throw new RuntimeException("Could not find class " + path, ex);
            }

        });
    }

    @Override
    public void loadComponentsFromClassLoader(ClassLoader classLoader) {
        try (InputStream in = classLoader.getResourceAsStream("META-INF/breeze-components-index.txt")) {

            if (in == null) return;
            String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            Optional<ComponentIndex> optionalIndex = ComponentIndex.fromJson(content);
            optionalIndex.ifPresent(index -> loadComponentsFromIndex(index, classLoader));

        } catch (Exception ex) {
            throw new RuntimeException("Failed to load components from index", ex);
        }
    }

    @Override
    public void loadComponentsFromIndex(ComponentIndex index, ClassLoader classLoader) {
        index.getEntries().forEach(entry -> {

            try {
                String path = entry.getClassPath();
                SupplyType supplyType = entry.getSupplyType();
                Class<?> clazz = classLoader.loadClass(path);

                ObjectSupplier supplier = SupplierFactory.create(supplyType, () -> inject(clazz));

                String name = clazz.getSimpleName().toLowerCase();
                addObjectSupplier(name, supplier);

            } catch (Exception ex) {
                throw new RuntimeException("Could not find class " + entry, ex);
            }

        });
    }

    @Override
    public void addSupplier(Object object) {
        Class<?> clazz = object.getClass();
        for (Method method : clazz.getDeclaredMethods()) {

            if (!method.isAnnotationPresent(Supply.class)) continue;

            Supply supply = method.getAnnotation(Supply.class);
            SupplyType type = supply.type();
            String key = method.getName().toLowerCase();

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

    private void addObjectSupplier(String key, ObjectSupplier supplier) {
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
        if (value == null) return !targetType.isPrimitive();
        if (targetType.isInstance(value)) return true;

        if (targetType.isPrimitive()) {
            Class<?> wrapper = primitiveWrappers.get(targetType);
            return wrapper != null && wrapper.isInstance(value);
        }

        return false;
    }

    @Override
    public <T> T inject(Class<T> clazz) {

        List<Constructor<?>> injectConstructors = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> c.isAnnotationPresent(Inject.class))
                .toList();

        if (injectConstructors.size() > 1) {
            throw new RuntimeException("Multiple @Inject constructors found in " + clazz.getName());
        }

        if (injectConstructors.isEmpty()) {
            throw new RuntimeException("No @Inject constructor found in " + clazz.getName());
        }

        Constructor<?> injectConstructor = injectConstructors.getFirst();

        try {
            if (injectConstructor != null) {
                injectConstructor.setAccessible(true);

                Parameter[] parameters = injectConstructor.getParameters();
                Object[] suppliedParameters = new Object[parameters.length];

                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    Class<?> type = parameter.getType();
                    String name = parameter.isAnnotationPresent(Named.class) ?
                            parameter.getAnnotation(Named.class).value() : parameter.getName();

                    Object supplied = get(type, name).orElseThrow(() ->
                            new RuntimeException("No supplier found for key: " + name + " with type: " + type)
                    );

                    if (!isAssignable(type, supplied)) {
                        throw new RuntimeException("Supplier for key " + name + " is not assignable to " + type);
                    }

                    suppliedParameters[i] = supplied;
                }

                return clazz.cast(injectConstructor.newInstance(suppliedParameters));

            } else {
                return clazz.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject dependencies for " + clazz.getName(), e);
        }
    }


}
