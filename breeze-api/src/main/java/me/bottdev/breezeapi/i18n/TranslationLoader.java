package me.bottdev.breezeapi.i18n;

import me.bottdev.breezeapi.config.ConfigLoader;
import me.bottdev.breezeapi.config.validation.ValidationResult;
import me.bottdev.breezeapi.config.validation.ValidationStatus;
import me.bottdev.breezeapi.config.validation.rules.StructureRule;
import me.bottdev.breezeapi.config.validation.types.RuleConfigValidator;
import me.bottdev.breezeapi.i18n.translations.ConfigTranslation;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezeapi.resource.ResourceTree;
import me.bottdev.breezeapi.resource.types.FileResource;
import me.bottdev.breezeapi.serialization.Mapper;
import me.bottdev.breezeapi.serialization.ObjectNode;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

public class TranslationLoader implements ConfigLoader<ConfigTranslation> {

    private final BreezeLogger logger = new SimpleLogger("TranslationLoader");
    private final Mapper serializationStrategy;
    private final RuleConfigValidator configValidator;


    public TranslationLoader(Mapper serializationStrategy) {
        this.serializationStrategy = serializationStrategy;
        this.configValidator = new RuleConfigValidator();
        configValidator.getRuleRegistry()
                .addRootRule(new StructureRule(ConfigTranslation.class));
    }

    @Override
    public Class<ConfigTranslation> getTargetClass() {
        return ConfigTranslation.class;
    }

    @Override
    public Optional<ConfigTranslation> load(FileResource resource) {

        logger.info("Loading translation {} from file {}", getTargetClass().getSimpleName(), resource.getName());

        if (!resource.getExtension().equalsIgnoreCase(serializationStrategy.getExtension())) {
            logger.info(
                    "Ignoring translation file {} because extension {} is unsupported.",
                    resource.getName(), resource.getExtension()
            );
            return Optional.empty();
        }

        Optional<String> serializedOptional = resource.read();
        if (serializedOptional.isEmpty()) return Optional.empty();

        String serialized = serializedOptional.get();

        if (serialized.isBlank()) {
            logger.warn("Could not load translation file, because file is empty or blank.");
            return Optional.empty();
        }

        Optional<ConfigTranslation> configOptional = load(serialized);
        if (configOptional.isPresent()) {
            logger.info("Loaded translation {} from file {}", getTargetClass().getSimpleName(), resource.getName());
            return configOptional;
        }

        return Optional.empty();

    }

    @Override
    public Optional<ConfigTranslation> load(String serialized) {

        Optional<ObjectNode> objectNodeOptional = serializationStrategy.deserializeTree(serialized);
        if (objectNodeOptional.isEmpty()) {
            logger.warn("Could not load translation file, because deserialized tree is empty.");
            return Optional.empty();
        }
        ObjectNode objectNode = objectNodeOptional.get();

        ValidationResult validationResult = configValidator.validate(objectNode);
        validationResult.logValidationResult(logger);

        ValidationStatus status = validationResult.getStatus();
        if (status != ValidationStatus.SUCCESS) {
            logger.warn("Could not load translation file, because translation validation failed.");
            return Optional.empty();
        }

        return serializationStrategy.deserialize(getTargetClass(), serialized);

    }

    @Override
    public ConfigTranslation loadOrDefault(FileResource resource, Supplier<ConfigTranslation> supplier) {
        logger.info("Loading or default translation {} from file {}", getTargetClass().getSimpleName(), resource.getName());
        Optional<ConfigTranslation> configOptional = load(resource);
        if (configOptional.isPresent()) {
            return configOptional.get();
        }
        logger.info("Passing default translation for {}", getTargetClass().getSimpleName());

        return supplier.get();
    }

    @Override
    public ConfigTranslation loadOrCreate(FileResource resource, Supplier<ConfigTranslation> supplier) {
        logger.info("Loading or creating translation {} from file {}", getTargetClass().getSimpleName(), resource.getName());
        Optional<ConfigTranslation> configOptional = load(resource);
        if (configOptional.isPresent()) {
            return configOptional.get();
        }
        logger.info("Creating translation {} from file {}", getTargetClass().getSimpleName(), resource.getName());

        ConfigTranslation suppliedConfiguration = supplier.get();
        save(resource, suppliedConfiguration);

        return supplier.get();
    }

    private Locale localeFromFilename(String filename) {

        String base = filename.substring(0, filename.lastIndexOf('.'));
        String tag = base.replace('_', '-');

        return Locale.forLanguageTag(tag);
    }

    public TranslationModule loadModule(String name, ResourceTree<? extends FileResource> resourceTree) {

        TranslationModule translationModule = new TranslationModule(name);

        resourceTree.getData().forEach((key, resource) -> {
            String fileName = resource.getName();
            Locale locale = localeFromFilename(fileName);
            load(resource).ifPresent(config -> {
                translationModule.addTranslation(locale, config);
            });
        });

        return translationModule;
    }

    @Override
    public void save(FileResource resource, ConfigTranslation configuration) {
        String serialized = serializationStrategy.serialize(configuration);
        resource.writeAndSave(serialized);
        logger.info("Saved translation {} into file {}", configuration.getClass().getSimpleName(), resource.getName());
    }

}
