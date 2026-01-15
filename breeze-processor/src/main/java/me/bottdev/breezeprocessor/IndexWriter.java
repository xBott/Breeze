package me.bottdev.breezeprocessor;

import me.bottdev.breezeapi.index.BreezeIndex;

public interface IndexWriter<T extends BreezeIndex> {

    void write(T index, String fileName);

}
