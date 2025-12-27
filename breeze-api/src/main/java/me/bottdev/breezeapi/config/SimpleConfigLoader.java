package me.bottdev.breezeapi.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.config.validation.ConfigValidator;
import me.bottdev.breezeapi.config.validation.ValidationResult;
import me.bottdev.breezeapi.config.validation.ValidationStatus;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezeapi.resource.types.FileResource;
import me.bottdev.breezeapi.serialization.Mapper;
import me.bottdev.breezeapi.serialization.ObjectNode;

import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class SimpleConfigLoader<T extends Configuration> implements ConfigLoader<T> {

    private final BreezeLogger logger = new SimpleLogger("SimpleConfigLoader");
    @Getter
    private final Class<T> targetClass;
    private final Mapper serializationStrategy;
    private final ConfigValidator configValidator;
    
    
    @Override
    public Optional<T> load(FileResource resource) {

        logger.info("Loading config {} from file {}", getTargetClass().getSimpleName(), resource.getName());

        if (!resource.getExtension().equalsIgnoreCase(serializationStrategy.getExtension())) {
            logger.info(
                    "Ignoring config file {} because extension {} is unsupported.",
                    resource.getName(), resource.getExtension()
            );
            return Optional.empty();
        }

        Optional<String> serializedOptional = resource.read();
        if (serializedOptional.isEmpty()) return Optional.empty();

        String serialized = serializedOptional.get();

        if (serialized.isBlank()) {
            logger.warn("Could not load config file, because file is empty or blank.");
            return Optional.empty();
        }

        Optional<T> configOptional = load(serialized);
        if (configOptional.isPresent()) {
            logger.info("Loaded config {} from file {}", getTargetClass().getSimpleName(), resource.getName());
            return configOptional;
        }

        return Optional.empty();

    }

    @Override
    public Optional<T> load(String serialized) {

        Optional<ObjectNode> objectNodeOptional = serializationStrategy.deserializeTree(serialized);
        if (objectNodeOptional.isEmpty()) {
            logger.warn("Could not load config file, because deserialized tree is empty.");
            return Optional.empty();
        }
        ObjectNode objectNode = objectNodeOptional.get();

        ValidationResult validationResult = configValidator.validate(objectNode);
        validationResult.logValidationResult(logger);

        ValidationStatus status = validationResult.getStatus();
        if (status != ValidationStatus.SUCCESS) {
            logger.warn("Could not load config file, because config validation failed.");
            return Optional.empty();
        }

        return serializationStrategy.deserialize(getTargetClass(), serialized);

    }

    @Override
    public T loadOrDefault(FileResource resource, Supplier<T> supplier) {
        logger.info("Loading or default config {} from file {}", getTargetClass().getSimpleName(), resource.getName());
        Optional<T> configOptional = load(resource);
        if (configOptional.isPresent()) {
            return configOptional.get();
        }
        logger.info("Passing default config for {}", getTargetClass().getSimpleName());

        return supplier.get();
    }

    @Override
    public T loadOrCreate(FileResource resource, Supplier<T> supplier) {
        logger.info("Loading or creating config {} from file {}", getTargetClass().getSimpleName(), resource.getName());
        Optional<T> configOptional = load(resource);
        if (configOptional.isPresent()) {
            return configOptional.get();
        }
        logger.info("Creating config {} from file {}", getTargetClass().getSimpleName(), resource.getName());

        Configuration suppliedConfiguration = supplier.get();
        save(resource, suppliedConfiguration);

        return supplier.get();
    }

    @Override
    public void save(FileResource resource, Configuration configuration) {
        String serialized = serializationStrategy.serialize(configuration);
        resource.writeAndSave(serialized);
        logger.info("Saved config {} into file {}", configuration.getClass().getSimpleName(), resource.getName());
    }

}
