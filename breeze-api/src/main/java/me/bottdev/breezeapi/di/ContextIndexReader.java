package me.bottdev.breezeapi.di;

import me.bottdev.breezeapi.di.exceptions.ContextReadException;
import me.bottdev.breezeapi.index.BreezeIndex;

public interface ContextIndexReader<T extends BreezeIndex> {

    Class<T> getIndexClass();

    void read(BreezeContext context, ClassLoader classLoader, T index) throws ContextReadException;

}
