package me.bottdev.breezeapi.di;

import me.bottdev.breezeapi.index.BreezeIndex;

public interface ContextIndexReader<T extends BreezeIndex> {

    Class<T> getIndexClass();

    void readIndex(BreezeContext context, ClassLoader classLoader, T index);

}
