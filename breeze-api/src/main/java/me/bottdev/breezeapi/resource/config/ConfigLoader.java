package me.bottdev.breezeapi.resource.config;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.resource.config.validation.ConfigStatus;
import me.bottdev.breezeapi.resource.config.validation.ConfigValidator;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleTreeLogger;
import me.bottdev.breezeapi.resource.types.FileResource;
import me.bottdev.breezeapi.serialization.Mapper;
import me.bottdev.breezeapi.serialization.ObjectNode;

import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class ConfigLoader {

    private final BreezeLogger logger = new SimpleTreeLogger("ConfigLoader");
    private final Mapper serializationStrategy;
    private final ConfigValidator configValidator;

//    private String readFile(File file) {
//
//        StringBuilder builder = new StringBuilder();
//
//        createFileIfNotExists(file);
//        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                builder.append(line);
//                builder.append(System.lineSeparator());
//            }
//
//        } catch (Exception ex) {
//            logger.error("Could not read config file.", ex);
//        }
//
//        return builder.toString();
//
//    }
//    private void writeFile(File file, String content) {
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
//
//            writer.write(content);
//
//        } catch (Exception ex) {
//            logger.error("Could not write config file.", ex);
//        }
//
//    }

    public <T extends Configuration> boolean canLoad(FileResource resource, Class<T> clazz) {

        Optional<String> serializedOptional = resource.read();
        if (serializedOptional.isEmpty()) return false;

        String serialized = serializedOptional.get();

        if (serialized.isBlank()) {
            return false;
        }

        Optional<ObjectNode> objectNodeOptional = serializationStrategy.deserializeTree(serialized);
        if (objectNodeOptional.isEmpty()) {
            return false;
        }
        ObjectNode objectNode = objectNodeOptional.get();

        ConfigStatus configStatus = configValidator.validateTree(objectNode, clazz);
        if (configStatus != ConfigStatus.SUCCESS) {
            return false;
        }

        return true;
    }


    public <T extends Configuration> Optional<T> loadConfig(FileResource resource, Class<T> clazz) {

        logger.info("Loading config {} from file {}", clazz.getSimpleName(), resource.getName());

        Optional<String> serializedOptional = resource.read();
        if (serializedOptional.isEmpty()) return Optional.empty();

        String serialized = serializedOptional.get();

        if (serialized.isBlank()) {
            logger.warn("Could not load config file, because file is empty or blank.");
            return Optional.empty();
        }

        Optional<ObjectNode> objectNodeOptional = serializationStrategy.deserializeTree(serialized);
        if (objectNodeOptional.isEmpty()) {
            logger.warn("Could not load config file, because deserialized tree is empty.");
            return Optional.empty();
        }
        ObjectNode objectNode = objectNodeOptional.get();

        ConfigStatus configStatus = configValidator.validateTree(objectNode, clazz);
        if (configStatus != ConfigStatus.SUCCESS) {
            logger.warn("Could not load config file, because config validation failed.");
            return Optional.empty();
        }

        logger.info("Loaded config {} from file {}", clazz.getSimpleName(), resource.getName());

        return serializationStrategy.deserialize(clazz, serialized);

    }

    public void saveConfig(FileResource resource, Configuration configuration) {
        String serialized = serializationStrategy.serialize(configuration);
        //resource save logic
        logger.info("Saved config {} into file {}", configuration.getClass().getSimpleName(), resource.getName());
    }

    public <T extends Configuration> T loadOrCreateConfig(FileResource resource, Class<T> clazz, Supplier<T> supplier) {
        logger.info("Loading or creating config {} from file {}", clazz.getSimpleName(), resource.getName());
        Optional<T> configOptional = loadConfig(resource, clazz);
        if (configOptional.isPresent()) {
            return configOptional.get();
        }
        logger.info("Creating config {} from file {}", clazz.getSimpleName(), resource.getName());

        Configuration suppliedConfiguration = supplier.get();
        saveConfig(resource, suppliedConfiguration);

        return supplier.get();
    }

    public <T extends Configuration> T loadOrDefault(FileResource resource, Class<T> clazz, Supplier<T> supplier) {
        logger.info("Loading or default config {} from file {}", clazz.getSimpleName(), resource.getName());
        Optional<T> configOptional = loadConfig(resource, clazz);
        if (configOptional.isPresent()) {
            return configOptional.get();
        }
        logger.info("Passing default config for {}", clazz.getSimpleName());

        return supplier.get();
    }

    public <T extends Configuration> T loadConfigSafely(FileResource resource, Class<T> clazz, Supplier<T> supplier) {
        logger.info("Loading config safely {} from file {}", clazz.getSimpleName(), resource.getName());

        if (!resource.getTempFile().exists()) {
            saveConfig(resource, supplier.get());
            return supplier.get();
        }
        return loadOrDefault(resource, clazz, supplier);

    }

}
