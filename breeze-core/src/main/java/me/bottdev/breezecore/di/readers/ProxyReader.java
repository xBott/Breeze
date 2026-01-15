package me.bottdev.breezecore.di.readers;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.commons.reflection.ReflectionCommons;
import me.bottdev.breezeapi.di.*;
import me.bottdev.breezeapi.di.proxy.ProxyFactoryRegistry;
import me.bottdev.breezeapi.index.types.ProxyIndex;
import me.bottdev.breezeapi.log.BreezeLogger;

import java.util.Optional;

@Deprecated
@RequiredArgsConstructor
public class ProxyReader implements ContextIndexReader<ProxyIndex> {

    private final BreezeLogger logger;
    private final ProxyFactoryRegistry factoryRegistry;

    @Override
    public Class<ProxyIndex> getIndexClass() {
        return ProxyIndex.class;
    }

    @Override
    public void read(BreezeContext context, ClassLoader classLoader, ProxyIndex index) {

        for (ProxyIndex.Entry entry : index.getEntries()) {

            try {

                String path = entry.getClassPath();
                Class<?> clazz = classLoader.loadClass(path);

                Optional<?> proxyOptional = factoryRegistry.createObject(clazz);
                if (proxyOptional.isEmpty()) continue;

                Object proxy = proxyOptional.get();

                //context.applyConstructHooks(proxy);

                String name = ReflectionCommons.asFieldName(clazz);

                //context.addObjectSupplier(name, new SingletonSupplier(proxy));
                logger.info("Proxy class {} has been created.", name);

            } catch (Exception ex) {
                logger.error("Could not load proxy class " + entry.getClassPath(), ex);
            }

        }

    }

}

