package me.bottdev.breezecore.di;

import me.bottdev.breezeapi.commons.structures.priority.PriorityList;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.ContextBootstrap;
import me.bottdev.breezeapi.di.ContextIndexReader;
import me.bottdev.breezeapi.index.BreezeIndex;
import me.bottdev.breezeapi.index.IndexMap;

public class SimpleContextBootstrap implements ContextBootstrap {

    private final PriorityList<ContextIndexReader<?>> readers = new PriorityList<>();

    @Override
    public SimpleContextBootstrap addReader(ContextIndexReader<?> reader, int priority) {
        readers.add(reader, priority);
        return this;
    }

    @Override
    public void bootstrap(BreezeContext context, ClassLoader classLoader, IndexMap bucket) {
        readers.stream().forEach(reader ->
                bucket.getIndices().stream()
                        .filter(index -> reader.getIndexClass().isAssignableFrom(index.getClass()))
                        .forEach(index -> callReader(reader, context, classLoader, index))
        );
    }

    private <T extends BreezeIndex> void callReader(
            ContextIndexReader<T> reader,
            BreezeContext context,
            ClassLoader classLoader,
            BreezeIndex index
    ) {
        T typedIndex = reader.getIndexClass().cast(index);
        reader.read(context, classLoader, typedIndex);
    }

}