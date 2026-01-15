package me.bottdev.breezeprocessor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.commons.dependency.Dependent;
import me.bottdev.breezeapi.index.types.ComponentIndex;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ComponentDependent implements Dependent {

    @Getter
    private final ComponentIndex.Entry entry;
    private final List<String> dependencies = new ArrayList<>();

    public void addDependencies(List<String> paths) {
        dependencies.addAll(paths);
    }

    @Override
    public String getDependentId() {
        return entry.getClassPath();
    }

    @Override
    public List<String> getDependencies() {
        return dependencies;
    }

}
