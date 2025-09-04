package me.bottdev.breezecore.modules;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.di.index.ComponentIndex;
import me.bottdev.breezeapi.di.index.SupplierIndex;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.modules.*;
import me.bottdev.breezeapi.log.SimpleLogger;
import me.bottdev.breezeapi.modules.Module;

import java.util.*;

@RequiredArgsConstructor
public class SimpleModuleManager implements ModuleManager {

    private final BreezeEngine engine;

    private final Set<ModuleLoader> moduleLoaders = new HashSet<>();
    private final Map<Class<? extends Module>, Module> loadedModules = new HashMap<>();
    private final BreezeLogger logger = new SimpleLogger("SimpleModuleManager");

    @Override
    public Set<ModuleLoader> getModuleLoaders() {
        return moduleLoaders;
    }

    @Override
    public void addModuleLoader(ModuleLoader moduleLoader) {
        moduleLoaders.add(moduleLoader);
    }

    @Override
    public List<Module> getModules() {
        return loadedModules.values().stream().toList();
    }

    @Override
    public <T extends Module> Optional<T> getModule(Class<T> moduleClass) {

        Module module = loadedModules.get(moduleClass);
        if (module == null) {
            return Optional.empty();
        }

        return Optional.of(moduleClass.cast(module));

    }

    @Override
    public boolean isModuleLoaded(Class<? extends Module> moduleClass) {
        return loadedModules.containsKey(moduleClass);
    }

    @Override
    public void unload(Class<? extends Module> moduleClass) {
        if (!isModuleLoaded(moduleClass)) {
            logger.info("Could not unloaded module {}, because it is not loaded.", moduleClass.getSimpleName());
            return;
        }
        Module module = loadedModules.remove(moduleClass);
        logger.info("Successfully unloaded module {}.", moduleClass.getSimpleName());
        disable(module);
    }

    @Override
    public void unloadAll() {
        logger.info("Unloading all modules...");
        loadedModules.values().forEach(this::disable);
        loadedModules.clear();
        logger.info("Successfully unloaded all modules");
    }

    private void loadContextFromModule(ModulePreLoad modulePreLoad) {
        BreezeContext context = engine.getContext();

        ClassLoader classLoader = modulePreLoad.getClassLoader();
        SupplierIndex supplierIndex = modulePreLoad.getSupplierIndex();
        ComponentIndex componentIndex = modulePreLoad.getComponentIndex();

        context.loadSuppliersFromIndex(supplierIndex, classLoader);
        context.loadComponentsFromIndex(componentIndex, classLoader);
    }

    @Override
    public void load(ModulePreLoad modulePreLoad) {

        Module module = modulePreLoad.getModule();
        String moduleName = modulePreLoad.getClass().getSimpleName();

        if (isModuleLoaded(module.getClass())) {
            logger.info("Module {} is already loaded. Unloading previously loaded module...", moduleName);
            return;
        }
        logger.info("Loading module {}...", moduleName);
        Class<? extends Module> moduleClass = module.getClass();
        loadedModules.put(moduleClass, module);
        loadContextFromModule(modulePreLoad);
        logger.info("Successfully loaded module {}.", moduleName);

        enable(module);
    }

    @Override
    public void loadAll() {
        logger.info("Loading modules from module loaders...");
        Set<ModuleLoader> loaders = getModuleLoaders();
        if (loaders.isEmpty()) {
            logger.warn("No module loaders found.");
            return;
        }
        loadedModules.clear();
        for (ModuleLoader loader : loaders) {
            String loaderName = loader.getClass().getSimpleName();
            List<ModulePreLoad> modulePreLoads = loader.load();
            for (ModulePreLoad modulePreLoad : modulePreLoads) {

                Module module = modulePreLoad.getModule();
                String moduleName = module.getClass().getSimpleName();

                loadedModules.put(module.getClass(), module);
                loadContextFromModule(modulePreLoad);

                logger.info("Successfully loaded module {} from module loader {}.", moduleName, loaderName);
            }
        }
        logger.info("Successfully loaded {}x from module loaders.", loadedModules.size());
        enableAll();
    }

    @Override
    public void enable(Module module) {
        String moduleName = module.getClass().getSimpleName();
        if (module.getStatus() == ModuleStatus.RUNNING) {
            logger.info("Could not enable module {}, because it's already enabled.", moduleName);
            return;
        }
        module.setStatus(ModuleStatus.ENABLING);
        logger.info("Enabling module {}...", moduleName);
        try {
            module.onEnable();
            module.setStatus(ModuleStatus.RUNNING);
            logger.info("Successfully enabled module {}.", moduleName);
        } catch (Exception ex) {
            module.setStatus(ModuleStatus.ERROR);
            throw new RuntimeException("Could not enable module: ", ex);
        }
    }

    @Override
    public void enableAll() {
        logger.info("Enabling all modules...");
        loadedModules.values().forEach(this::enable);
    }

    @Override
    public void disable(Module module) {
        String moduleName = module.getClass().getSimpleName();
        if (module.getStatus() != ModuleStatus.RUNNING) {
            logger.info("Could not disable module {}, because it's not enabled.", moduleName);
            return;
        }
        module.setStatus(ModuleStatus.DISABLING);
        logger.info("Disabling module {}...", moduleName);
        try {
            module.onDisable();
            module.setStatus(ModuleStatus.DISABLED);
            logger.info("Successfully disabled module {}.", moduleName);
        } catch (Exception ex) {
            module.setStatus(ModuleStatus.ERROR);
            throw new RuntimeException("Could not disable module: ", ex);
        }
    }

    @Override
    public void disableAll() {
        logger.info("Disabling all modules...");
        loadedModules.values().forEach(this::disable);
    }

    @Override
    public void restart(Module module) {
        String moduleName = module.getClass().getSimpleName();
        if (module.getStatus() != ModuleStatus.RUNNING) {
            logger.info("Could not restart module {}, because it's not enabled.", module.getClass().getSimpleName());
            return;
        }
        module.setStatus(ModuleStatus.RESTARTING);
        logger.info("Restarting module {}...", moduleName);
        try {
            module.onRestart();
            module.setStatus(ModuleStatus.RUNNING);
            logger.info("Successfully restarted module {}.", moduleName);
        } catch (Exception ex) {
            module.setStatus(ModuleStatus.ERROR);
            throw new RuntimeException("Could not restart module: ", ex);
        }
    }

    @Override
    public void restartAll() {
        logger.info("Restarting all modules...");
        loadedModules.values().forEach(this::restart);
    }

}
