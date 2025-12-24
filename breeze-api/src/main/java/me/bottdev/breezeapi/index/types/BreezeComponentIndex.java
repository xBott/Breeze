package me.bottdev.breezeapi.index.types;

import lombok.*;
import me.bottdev.breezeapi.di.dependency.Dependent;
import me.bottdev.breezeapi.di.dependency.DependentContainer;
import me.bottdev.breezeapi.di.SupplyType;
import me.bottdev.breezeapi.index.IndexEntry;

import java.util.ArrayList;
import java.util.List;

public class BreezeComponentIndex implements
        MultipleBreezeIndex<BreezeComponentIndex.Entry>,
        DependentContainer<BreezeComponentIndex.Entry>
{

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry implements IndexEntry, Dependent {

        private String classPath;
        private SupplyType supplyType;
        @Singular("dependency")
        private List<String> dependencies;

        @Override
        public String getDependentId() {
            return classPath;
        }
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

    @Override
    public List<Entry> getDependents() {
        return entries;
    }

}
