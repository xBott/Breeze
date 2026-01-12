package me.bottdev.breezecore.modules;

import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.di.ContextBootstrapper;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.modules.*;
import me.bottdev.breezeapi.modules.Module;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class SimpleModuleManager extends ModuleManager {

    private final Set<ModuleLoader> moduleLoaders = new HashSet<>();
    private final Map<Class<? extends Module>, Module> loadedModules = new HashMap<>();

    private final BreezeEngine breezeEngine;
    private final BreezeLogger mainLogger;

    @Inject
    public SimpleModuleManager(BreezeEngine breezeEngine, BreezeLogger mainLogger) {
        this.breezeEngine = breezeEngine;
        this.mainLogger = mainLogger;
    }

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
        String moduleName = moduleClass.getSimpleName();
        mainLogger.withSection("Unloading module " + moduleName, "", () -> {

            if (!isModuleLoaded(moduleClass)) {
                mainLogger.info("Could not unloaded module {}, because it is not loaded.", moduleClass.getSimpleName());
                mainLogger.pop();
                return;
            }
            Module module = loadedModules.remove(moduleClass);
            mainLogger.info("Successfully unloaded module {}.", moduleClass.getSimpleName());
            disable(module);

        });
    }

    @Override
    public void unloadAll() {
        mainLogger.withSection("Unloading all modules", "", () -> {

            loadedModules.values().forEach(this::disable);
            loadedModules.clear();
            mainLogger.info("Successfully unloaded all modules");

        });
    }

    private void loadContextFromModule(ModulePreLoad modulePreLoad) {

        String moduleName = modulePreLoad.getModuleClass().getSimpleName();
        mainLogger.withSection("Loading context from module " +  moduleName, "", () -> {

            BreezeContext context = breezeEngine.getContext();
            ContextBootstrapper contextBootstrapper = breezeEngine.getContextBootstrapper();

            ClassLoader classLoader = modulePreLoad.getClassLoader();

            contextBootstrapper.bootstrap(context, classLoader, modulePreLoad.getIndexBucket());

        });

    }

    private Optional<Module> handleModulePreLoad(ModulePreLoad modulePreLoad) {
        String moduleName = modulePreLoad.getModuleClass().getSimpleName();

        if (isModuleLoaded(modulePreLoad.getModuleClass())) {
            mainLogger.info("Module {} is already loaded. Unloading previously loaded module...", moduleName);
            return Optional.empty();
        }

        Supplier<Optional<Module>> supplier = modulePreLoad.getModuleSupplier();

        loadContextFromModule(modulePreLoad);

        Optional<Module> moduleOptional = supplier.get();
        if (moduleOptional.isEmpty()) {
            mainLogger.info("Could not load Module, supplier is empty.");
            return Optional.empty();
        }

        Module module = moduleOptional.get();
        breezeEngine.getContext().injectFields(module);

        mainLogger.info("Loading module {}...", moduleName);
        Class<? extends Module> moduleClass = module.getClass();

        loadedModules.put(moduleClass, module);

        return Optional.of(module);
    }

    @Override
    public void load(ModulePreLoad modulePreLoad) {

        String moduleName = modulePreLoad.getModuleClass().getSimpleName();
        mainLogger.withSection("Loading module " + moduleName, "", () -> {

            Optional<Module> moduleOptional = handleModulePreLoad(modulePreLoad);
            moduleOptional.ifPresent(module -> {
                mainLogger.info("Successfully loaded module {}.", moduleName);
                enable(module);
            });

        });

    }

    @Override
    public void loadAll() {

        mainLogger.withSection("Loading modules from module loaders", "", () -> {

            Set<ModuleLoader> loaders = getModuleLoaders();
            if (loaders.isEmpty()) {
                mainLogger.warn("No module loaders found.");
                mainLogger.pop();
                return;
            }

            loadedModules.clear();

            for (ModuleLoader loader : loaders) {

                String loaderName = loader.getClass().getSimpleName();
                List<ModulePreLoad> modulePreLoads = loader.load();

                for (ModulePreLoad modulePreLoad : modulePreLoads) {
                    String moduleName = modulePreLoad.getModuleClass().getSimpleName();
                    boolean loaded = loadSingleModuleFromLoader(modulePreLoad);
                    if (loaded) {
                        mainLogger.info("Successfully loaded module {} from module loader {}.", moduleName, loaderName);
                    }
                }

            }

            mainLogger.info("Successfully loaded {}x from module loaders.", loadedModules.size());
        });

        enableAll();

    }

    private boolean loadSingleModuleFromLoader(ModulePreLoad modulePreLoad) {

        AtomicBoolean loaded = new AtomicBoolean(false);

        String moduleName = modulePreLoad.getModuleClass().getSimpleName();
        mainLogger.withSection("Loading module " + moduleName + " from loader", "", () -> {

            Optional<Module> moduleOptional = handleModulePreLoad(modulePreLoad);
            moduleOptional.ifPresent(module -> loaded.set(true));

        });

        return loaded.get();
    }

    @Override
    public void enable(Module module) {

        String moduleName = module.getClass().getSimpleName();
        mainLogger.withSection("Enabling module " + moduleName, "", () -> {

            if (module.getStatus() == ModuleStatus.RUNNING) {
                mainLogger.info("Could not enable module {}, because it's already enabled.", moduleName);
                return;
            }

            module.setStatus(ModuleStatus.ENABLING);

            try {
                module.onEnable();
                module.setStatus(ModuleStatus.RUNNING);
                mainLogger.info("Successfully enabled module {}.", moduleName);
            } catch (Exception ex) {
                module.setStatus(ModuleStatus.ERROR);
                mainLogger.error("Could not enable module: ", ex);
            }

        });

    }

    @Override
    public void enableAll() {
        mainLogger.withSection("Enabling modules", "", () -> {
            loadedModules.values().forEach(this::enable);
        });
    }

    @Override
    public void disable(Module module) {

        String moduleName = module.getClass().getSimpleName();
        mainLogger.withSection("Disabling module " + moduleName, "", () -> {

            if (module.getStatus() != ModuleStatus.RUNNING) {
                mainLogger.info("Could not disable module {}, because it's not enabled.", moduleName);
                return;
            }

            module.setStatus(ModuleStatus.DISABLING);

            try {
                module.onDisable();
                module.setStatus(ModuleStatus.DISABLED);
                mainLogger.info("Successfully disabled module {}.", moduleName);
            } catch (Exception ex) {
                module.setStatus(ModuleStatus.ERROR);
                mainLogger.error("Could not disable module: ", ex);
            }

        });

    }

    @Override
    public void disableAll() {
        mainLogger.withSection("Disabling modules", "", () -> {
            loadedModules.values().forEach(this::disable);
        });
    }

    @Override
    public void restart(Module module) {

        String moduleName = module.getClass().getSimpleName();
        mainLogger.withSection("Restarting module " + moduleName, "", () -> {

            if (module.getStatus() != ModuleStatus.RUNNING) {
                mainLogger.info("Could not restart module {}, because it's not enabled.", module.getClass().getSimpleName());
                return;
            }

            module.setStatus(ModuleStatus.RESTARTING);

            try {
                module.onRestart();
                module.setStatus(ModuleStatus.RUNNING);
                mainLogger.info("Successfully restarted module {}.", moduleName);
            } catch (Exception ex) {
                module.setStatus(ModuleStatus.ERROR);
                mainLogger.error("Could not restart module: ", ex);
            }

        });

    }

    @Override
    public void restartAll() {
        mainLogger.withSection("Restarting all modules", "", () -> {
            loadedModules.values().forEach(this::restart);
        });
    }

}
