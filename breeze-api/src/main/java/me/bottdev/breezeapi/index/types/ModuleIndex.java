package me.bottdev.breezeapi.index.types;

import lombok.*;
import me.bottdev.breezeapi.commons.dependency.Dependent;
import me.bottdev.breezeapi.index.SingleIndex;

import java.util.List;

@Getter
@Setter
public class ModuleIndex implements SingleIndex, Dependent {

    private String classPath;
    private String moduleName;
    private String version;
    private List<String> dependencies;

    @Override
    public String getDependentId() {
        return classPath;
    }

    @Override
    public List<String> getDependencies() {
        return dependencies;
    }
}
