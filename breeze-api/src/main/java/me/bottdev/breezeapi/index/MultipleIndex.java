package me.bottdev.breezeapi.index;

import java.util.List;

public interface MultipleIndex<E extends IndexEntry> extends BreezeIndex {

    List<E> getEntries();

    boolean hasEntry(E entry);

    void addEntry(E entry);

    void addEntries(List<E> entries);

}
