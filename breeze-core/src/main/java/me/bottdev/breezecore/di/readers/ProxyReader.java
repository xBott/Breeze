package me.bottdev.breezecore.di.readers;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.di.*;
import me.bottdev.breezeapi.di.proxy.ProxyFactory;
import me.bottdev.breezeapi.di.suppliers.SingletonSupplier;
import me.bottdev.breezeapi.index.types.BreezeProxyIndex;
import me.bottdev.breezeapi.log.BreezeLogger;

import java.util.Optional;

@RequiredArgsConstructor
public class ProxyReader implements ContextIndexReader<BreezeProxyIndex> {

    private final BreezeLogger logger;
    private final ProxyFactory proxyFactory;

    @Override
    public Class<BreezeProxyIndex> getIndexClass() {
        return BreezeProxyIndex.class;
    }

    @Override
    public void readIndex(BreezeContext context, ClassLoader classLoader, BreezeProxyIndex index) {

        for (BreezeProxyIndex.Entry entry : index.getEntries()) {

            try {

                String path = entry.getClassPath();
                Class<?> clazz = classLoader.loadClass(path);

                Optional<?> proxyOptional = proxyFactory.create(clazz);
                if (proxyOptional.isEmpty()) continue;

                Object proxy = proxyOptional.get();

                context.applyConstructHooks(proxy);

                String name = clazz.getSimpleName();
                name = name.substring(0, 1).toLowerCase() + name.substring(1);

                context.addObjectSupplier(name, new SingletonSupplier(proxy));
                logger.info("Proxy class {} has been created.", name);

            } catch (Exception ex) {
                logger.error("Could not load proxy class " + entry.getClassPath(), ex);
            }

        }

    }

}

