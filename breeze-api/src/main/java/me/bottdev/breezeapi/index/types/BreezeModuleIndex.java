package me.bottdev.breezeapi.index.types;

import lombok.*;
import me.bottdev.breezeapi.dependency.Dependent;

import java.util.List;

@Getter
@Setter
public class BreezeModuleIndex implements SingleBreezeIndex, Dependent {

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
