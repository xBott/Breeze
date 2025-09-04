package me.bottdev.breezeapi.modules;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ModuleManager {

    Set<ModuleLoader> getModuleLoaders();

    void addModuleLoader(ModuleLoader moduleLoader);

    List<Module> getModules();

    <T extends Module> Optional<T> getModule(Class<T> moduleClass);

    boolean isModuleLoaded(Class<? extends Module> moduleClass);

    void unload(Class<? extends Module> moduleClass);

    void unloadAll();

    void load(ModulePreLoad modulePreLoad);

    void loadAll();

    void enable(Module module);

    void enableAll();

    void disable(Module module);

    void disableAll();

    void restart(Module module);

    void restartAll();

}
