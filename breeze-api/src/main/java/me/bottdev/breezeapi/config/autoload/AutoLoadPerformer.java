package me.bottdev.breezeapi.config.autoload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.config.ConfigLoader;
import me.bottdev.breezeapi.config.Configuration;
import me.bottdev.breezeapi.config.validation.ConfigValidator;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.suppliers.SingletonSupplier;
import me.bottdev.breezeapi.index.types.BreezeAutoLoadIndex;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleLogger;
import me.bottdev.breezeapi.modules.ModulePreLoad;
import me.bottdev.breezeapi.serialization.Mapper;

import java.io.File;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class AutoLoadPerformer {

    private static final BreezeLogger logger = new SimpleLogger("AutoLoadPerformer");

    private final BreezeEngine engine;
    private final ModulePreLoad modulePreLoad;
    private final ClassLoader classLoader;

    public void load(BreezeAutoLoadIndex index) {
        logger.info("Loading auto load configurations...");
        for (BreezeAutoLoadIndex.Entry entry : index.getEntries()) {
            Optional<Configuration> configurationOptional = loadEntry(entry);
            configurationOptional.ifPresent(this::addConfigurationSupplier);
        }
        logger.info("Finished loading auto load configurations.");
    }

    @SuppressWarnings("unchecked")
    private Optional<Configuration> loadEntry(BreezeAutoLoadIndex.Entry entry) {

//        try {
//
//            String classPath = entry.getClassPath();
//            Class<? extends Configuration> clazz = (Class<? extends Configuration>) classLoader.loadClass(classPath);
//
//            AutoLoadSerializer serializer = entry.getSerializer();
//            Mapper mapper = serializer.getMapper(engine);
//
//            String filePath = entry.getFilePath();
//            String formattedPath = replacePathPlaceholders(filePath, clazz, serializer);
//            File file = new File(formattedPath);
//
//            logger.info("Trying to load {} {} configuration from {}...",
//                    serializer.toString(), clazz.getSimpleName(), formattedPath);
//
//            ConfigLoader configLoader = new ConfigLoader(mapper, new ConfigValidator());
//
//            Configuration emptyInstance;
//            try {
//                emptyInstance = clazz.getDeclaredConstructor().newInstance();
//            } catch (Exception ex) {
//                emptyInstance = null;
//                logger.error("Failed to create empty instance of " + clazz.getSimpleName(), ex);
//            }
//
//            Configuration finalEmptyInstance = emptyInstance;
//
//            Configuration configuration = configLoader.loadConfigSafely(
//                    file,
//                    (Class<Configuration>) clazz,
//                    () -> finalEmptyInstance
//            );
//
//            return Optional.of(configuration);
//
//
//        } catch (Exception ex) {
//            logger.error("Failed to load auto load entry: " + entry.getFilePath(), ex);
//        }

        return Optional.empty();
    }

    private String replacePathPlaceholders(String path, Class<? extends Configuration> clazz, AutoLoadSerializer serializer) {
        return path
                .replace("{engine}", engine.getDataFolder().toString())
                .replace("{module}", modulePreLoad.getModuleDataFolder().toString())
                .replace("{name}", clazz.getSimpleName())
                .replace("{extension}", serializer.getExtension());
    }

    private <T extends Configuration> void addConfigurationSupplier(T configuration) {
        BreezeContext context = engine.getContext();

        String name = configuration.getClass().getSimpleName();
        name = name.substring(0, 1).toLowerCase() + name.substring(1);

        context.addObjectSupplier(name, new SingletonSupplier(configuration));

        logger.info("Auto load configuration supplier: " + name);
    }

}
