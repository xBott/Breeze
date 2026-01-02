package me.bottdev.breezeapi.modules;

import me.bottdev.breezeapi.lifecycle.Lifecycle;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class ModuleManager extends Lifecycle {

    @Override
    protected void onStart() {
        loadAll();
    }

    @Override
    protected void onShutdown() {
        unloadAll();
    }

    public abstract Set<ModuleLoader> getModuleLoaders();

    public abstract void addModuleLoader(ModuleLoader moduleLoader);

    public abstract List<Module> getModules();

    public abstract <T extends Module> Optional<T> getModule(Class<T> moduleClass);

    public abstract boolean isModuleLoaded(Class<? extends Module> moduleClass);

    public abstract void unload(Class<? extends Module> moduleClass);

    public abstract void unloadAll();

    public abstract void load(ModulePreLoad modulePreLoad);

    public abstract void loadAll();

    public abstract void enable(Module module);

    public abstract void enableAll();

    public abstract void disable(Module module);

    public abstract void disableAll();

    public abstract void restart(Module module);

    public abstract void restartAll();

}
