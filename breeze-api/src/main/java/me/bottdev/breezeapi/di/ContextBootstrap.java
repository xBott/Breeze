package me.bottdev.breezeapi.di;

import me.bottdev.breezeapi.index.IndexMap;

public interface ContextBootstrap {

    ContextBootstrap addReader(ContextIndexReader<?> reader, int priority);

    void bootstrap(BreezeContext context, ClassLoader classLoader, IndexMap bucket);

}