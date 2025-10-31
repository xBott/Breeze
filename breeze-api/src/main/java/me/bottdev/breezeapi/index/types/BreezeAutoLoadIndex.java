package me.bottdev.breezeapi.index.types;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import me.bottdev.breezeapi.config.autoload.AutoLoadSerializer;
import me.bottdev.breezeapi.dependency.Dependent;
import me.bottdev.breezeapi.di.SupplyType;
import me.bottdev.breezeapi.index.BreezeIndex;
import me.bottdev.breezeapi.index.IndexEntry;

import java.util.ArrayList;
import java.util.List;

public class BreezeAutoLoadIndex implements MultipleBreezeIndex<BreezeAutoLoadIndex.Entry> {

    @Getter
    @Builder
    public static class Entry implements IndexEntry {

        private String classPath;
        private String filePath;
        private AutoLoadSerializer serializer;

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
