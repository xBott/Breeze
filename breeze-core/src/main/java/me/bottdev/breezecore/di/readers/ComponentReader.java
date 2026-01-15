package me.bottdev.breezecore.di.readers;

import me.bottdev.breezeapi.commons.reflection.ReflectionCommons;
import me.bottdev.breezeapi.di.*;
import me.bottdev.breezeapi.di.exceptions.ContextReadException;
import me.bottdev.breezeapi.index.types.ComponentIndex;

public class ComponentReader implements ContextIndexReader<ComponentIndex> {

    @Override
    public Class<ComponentIndex> getIndexClass() {
        return ComponentIndex.class;
    }

    @Override
    public void read(BreezeContext context, ClassLoader classLoader, ComponentIndex index)
            throws ContextReadException
    {

        index.getEntries().forEach(entry -> {
            try {

                String path = entry.getClassPath();
                BeanScope scope = entry.getScope();
                Class<?> clazz = classLoader.loadClass(path);
                String name = ReflectionCommons.asFieldName(clazz);

                context.bind(clazz)
                        .scope(scope)
                        .qualified(name)
                        .self();

            } catch (Exception ex) {
                throw new ContextReadException("Could not bind component: " + entry.getClassPath(), ex);
            }
        });

    }
}

