package me.bottdev.breezeapi.config;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.config.validation.ConfigStatus;
import me.bottdev.breezeapi.config.validation.ConfigValidator;
import me.bottdev.breezeapi.config.validation.FieldStatus;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleLogger;
import me.bottdev.breezeapi.serialization.Mapper;
import me.bottdev.breezeapi.serialization.ObjectNode;

import javax.swing.text.html.Option;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Optional;

@RequiredArgsConstructor
public class ConfigLoader {

    private final BreezeLogger logger = new SimpleLogger("ConfigLoader");
    private final Mapper serializationStrategy;
    private final ConfigValidator configValidator;

    private void createFileIfNotExists(File file) {
        if (file.exists()) return;
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (Exception ex) {
            logger.error("Could not create config file.", ex);
        }
    }

    private String readFile(File file) {

        StringBuilder builder = new StringBuilder();

        createFileIfNotExists(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.lineSeparator());
            }

        } catch (Exception ex) {
            logger.error("Could not read config file.", ex);
        }

        return builder.toString();

    }

    public <T extends Configuration> Optional<T> loadConfig(File file, Class<T> clazz) {

        String serialized = readFile(file);
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

        return serializationStrategy.deserialize(clazz, serialized);

    }

}
