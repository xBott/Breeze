package me.bottdev.breezecore.di.readers;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.commons.reflection.ReflectionCommons;
import me.bottdev.breezeapi.di.*;
import me.bottdev.breezeapi.index.types.BreezeComponentIndex;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezecore.di.resolver.ComponentDependencyResolver;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ComponentReader implements ContextIndexReader<BreezeComponentIndex> {

    private final BreezeLogger logger;
    private final ComponentDependencyResolver resolver;

    @Override
    public Class<BreezeComponentIndex> getIndexClass() {
        return BreezeComponentIndex.class;
    }

    @Override
    public void readIndex(BreezeContext context, ClassLoader classLoader, BreezeComponentIndex index) {
        List<BreezeComponentIndex.Entry> resolvedDependencies = resolver.resolve(index);

        for (BreezeComponentIndex.Entry dependency : resolvedDependencies) {
            try {
                String path = dependency.getClassPath();
                SupplyType supplyType = dependency.getSupplyType();
                Class<?> clazz = classLoader.loadClass(path);
                String name = ReflectionCommons.asFieldName(clazz);

                context.createComponent(name, supplyType, clazz);

            } catch (Exception ex) {
                logger.error("Could not find component class " + dependency, ex);
            }
        }
    }
}

