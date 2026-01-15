package me.bottdev.breezeapi.index.types;

import lombok.*;
import me.bottdev.breezeapi.di.BeanScope;
import me.bottdev.breezeapi.index.IndexEntry;
import me.bottdev.breezeapi.index.MultipleIndex;

import java.util.ArrayList;
import java.util.List;

public class ComponentIndex implements
        MultipleIndex<ComponentIndex.Entry>
{

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry implements IndexEntry {

        private String classPath;
        private BeanScope scope;

    }

    @Getter
    private final List<Entry> entries = new ArrayList<>();

    @Override
    public boolean hasEntry(Entry entry) {
        return entries.contains(entry);
    }

    @Override
    public void addEntry(Entry entry) {
        if (hasEntry(entry)) return;
        entries.add(entry);
    }

    @Override
    public void addEntries(List<Entry> entries) {
        for (Entry entry : entries) {
            addEntry(entry);
        }
    }

}
