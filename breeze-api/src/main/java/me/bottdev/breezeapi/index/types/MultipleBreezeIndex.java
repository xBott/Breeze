package me.bottdev.breezeapi.index.types;

import me.bottdev.breezeapi.index.BreezeIndex;
import me.bottdev.breezeapi.index.IndexEntry;

import java.util.List;

public interface MultipleBreezeIndex<E extends IndexEntry> extends BreezeIndex {

    List<E> getEntries();

    boolean hasEntry(E entry);

    void addEntry(E entry);

    void addEntries(List<E> entries);

}
