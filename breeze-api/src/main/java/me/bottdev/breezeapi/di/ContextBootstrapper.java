package me.bottdev.breezeapi.di;

import me.bottdev.breezeapi.index.BreezeIndex;
import me.bottdev.breezeapi.index.BreezeIndexBucket;

import java.util.*;

public class ContextBootstrapper {

    private final Map<Class<? extends BreezeIndex>, ContextIndexReader<?>> readers = new HashMap<>();

    public <T extends BreezeIndex> ContextBootstrapper addReader(ContextIndexReader<T> reader) {
        readers.put(reader.getIndexClass(), reader);
        return this;
    }

    public void bootstrap(BreezeContext context, ClassLoader classLoader, BreezeIndexBucket bucket) {
        bucket.getIndices().forEach(index -> {
            Class<? extends BreezeIndex> indexClass = index.getClass();
            ContextIndexReader<?> reader = readers.get(indexClass);
            if (reader == null) return;

            callReader(reader, context, classLoader, index);
        });
    }

    private <T extends BreezeIndex> void callReader(
            ContextIndexReader<T> reader,
            BreezeContext ctx,
            ClassLoader cl,
            BreezeIndex index
    ) {
        T typedIndex = reader.getIndexClass().cast(index);
        reader.readIndex(ctx, cl, typedIndex);
    }

}
