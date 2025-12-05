package me.bottdev.breezecore.di.readers;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.di.ContextIndexReader;
import me.bottdev.breezeapi.di.BreezeContext;
    import me.bottdev.breezeapi.index.types.BreezeSupplierIndex;
import me.bottdev.breezeapi.log.BreezeLogger;

@RequiredArgsConstructor
public class SupplierReader implements ContextIndexReader<BreezeSupplierIndex> {

    private final BreezeLogger logger;

    @Override
    public Class<BreezeSupplierIndex> getIndexClass() {
        return BreezeSupplierIndex.class;
    }

    @Override
    public void readIndex(BreezeContext context, ClassLoader classLoader, BreezeSupplierIndex index) {
        index.getEntries().forEach(entry -> {
            try {
                Class<?> clazz = classLoader.loadClass(entry.getClassPath());
                context.addSupplier(clazz.getDeclaredConstructor().newInstance());
            } catch (Exception ex) {
                logger.error("Could not load supplier class " + entry.getClassPath(), ex);
            }
        });
    }
}

