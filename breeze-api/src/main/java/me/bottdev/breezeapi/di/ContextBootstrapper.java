package me.bottdev.breezeapi.di;

import me.bottdev.breezeapi.index.BreezeIndex;
import me.bottdev.breezeapi.index.BreezeIndexBucket;

import java.util.*;
import java.util.stream.Collectors;

public class ContextBootstrapper {

    private final List<ReaderPriorityWrapper> readers = new ArrayList<>();

    public ContextBootstrapper addReader(ContextIndexReader<?> reader, int priority) {
        readers.add(new ReaderPriorityWrapper(reader, priority));
        return this;
    }

    public void bootstrap(BreezeContext context, ClassLoader classLoader, BreezeIndexBucket bucket) {

        List<ContextIndexReader<?>> sortedReaders = readers.stream()
                .sorted(Comparator.comparing(ReaderPriorityWrapper::getPriority))
                .map(ReaderPriorityWrapper::getReader)
                .collect(Collectors.toList());

        sortedReaders.forEach(reader -> {
            bucket.getIndices().stream()
                    .filter(index -> reader.getIndexClass().isAssignableFrom(index.getClass()))
                    .forEach(index -> callReader(reader, context, classLoader, index));
        });

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
