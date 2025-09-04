package me.bottdev.breezeapi.serialization.polymorphic;

import com.fasterxml.jackson.databind.module.SimpleModule;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleLogger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PolymorphicType<T> {

    private final BreezeLogger logger;
    private final Class<T> baseClass;

    public PolymorphicType(Class<T> baseClass) {
        this.baseClass = baseClass;
        this.logger = new SimpleLogger("PolymorphicRegistry-%s".formatted(baseClass.getSimpleName()));
    }

    private final Map<String, Class<? extends T>> registeredClasses = new HashMap<>();

    public Set<Class<? extends T>> getRegisteredClasses() {
        return new HashSet<>(registeredClasses.values());
    }

    public boolean isRegistered(Class<? extends T> clazz) {
        return registeredClasses.containsKey(clazz.getSimpleName());
    }

    public PolymorphicType<T> registerSubtype(Class<? extends T> subClass) {
        String className = subClass.getSimpleName();
        if (isRegistered(subClass)) {
            logger.info("Type {} is already registered, overriding...", className);
        }
        registeredClasses.put(className, subClass);
        return this;
    }

    public SimpleModule getModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(baseClass, new PolymorphicSerializer("type"));
        module.addDeserializer(baseClass, new PolymorphicDeserializer<>(baseClass, registeredClasses, "type"));
        return module;
    }

}
