package me.bottdev.breezeapi.di;

import me.bottdev.breezeapi.commons.structures.priority.PriorityList;
import me.bottdev.breezeapi.index.BreezeIndex;
import me.bottdev.breezeapi.index.BreezeIndexBucket;

public class ContextBootstrapper {

    private final PriorityList<ContextIndexReader<?>> readers = new PriorityList<>();

    public ContextBootstrapper addReader(ContextIndexReader<?> reader, int priority) {
        readers.add(reader, priority);
        return this;
    }

    public void bootstrap(BreezeContext context, ClassLoader classLoader, BreezeIndexBucket bucket) {
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
        reader.readIndex(context, classLoader, typedIndex);
    }

}