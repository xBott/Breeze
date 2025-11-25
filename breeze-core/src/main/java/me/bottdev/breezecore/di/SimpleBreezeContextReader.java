package me.bottdev.breezecore.di;

import lombok.Getter;
import me.bottdev.breezeapi.di.*;
import me.bottdev.breezeapi.index.BreezeIndexSerializer;
import me.bottdev.breezeapi.index.types.BreezeComponentIndex;
import me.bottdev.breezeapi.index.types.BreezeSupplierIndex;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezecore.di.resolver.ComponentDependencyResolver;

import java.util.List;
import java.util.Optional;

@Getter
public class SimpleBreezeContextReader implements ContextReader {

    private final BreezeContext context;
    private final BreezeIndexSerializer serializer;
    private final ComponentDependencyResolver resolver;
    private final BreezeLogger logger;

    public SimpleBreezeContextReader(BreezeContext context, BreezeLogger logger) {
        this.context = context;
        this.logger = logger;
        this.serializer = new BreezeIndexSerializer();
        this.resolver = new ComponentDependencyResolver(logger);
    }

    @Override
    public void read() {
        ClassLoader classLoader = getClass().getClassLoader();
        read(classLoader);
    }

    @Override
    public void readSuppliersFromIndex(BreezeSupplierIndex index, ClassLoader classLoader) {
        index.getEntries().forEach(entry -> {

            String classPath = entry.getClassPath();

            try {
                Class<?> clazz = classLoader.loadClass(classPath);
                getContext().addSupplier(clazz.getDeclaredConstructor().newInstance());

            } catch (Exception ex) {
                getContext().getLogger().error("Could not find supplier class " + classPath, ex);
            }

        });
    }

    @Override
    public void readComponentsFromIndex(BreezeComponentIndex index, ClassLoader classLoader) {
        List<BreezeComponentIndex.Entry> resolvedDependencies = resolver.resolve(index);

        for (BreezeComponentIndex.Entry dependency : resolvedDependencies) {
            try {
                String path = dependency.getClassPath();
                SupplyType supplyType = dependency.getSupplyType();
                Class<?> clazz = classLoader.loadClass(path);

                Optional<?> optionalInjectedObject = getContext().injectConstructor(clazz);
                if (optionalInjectedObject.isEmpty()) continue;

                ObjectSupplier supplier = SupplierFactory.create(
                        supplyType,
                        () -> getContext().injectConstructorAndApplyHooks(clazz).orElseThrow()
                );

                String name = clazz.getSimpleName();
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
                getContext().addObjectSupplier(name, supplier);

            } catch (Exception ex) {
                getContext().getLogger().error("Could not find component class " + dependency, ex);
            }
        }

    }
}
