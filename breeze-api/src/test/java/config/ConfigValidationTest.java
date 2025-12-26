package config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.bottdev.breezeapi.config.ConfigLoader;
import me.bottdev.breezeapi.config.Configuration;
import me.bottdev.breezeapi.config.validation.ValidationError;
import me.bottdev.breezeapi.config.validation.ValidationNode;
import me.bottdev.breezeapi.config.validation.ValidationResult;
import me.bottdev.breezeapi.config.validation.ValidationStatus;
import me.bottdev.breezeapi.config.validation.patterns.PathEndsPattern;
import me.bottdev.breezeapi.config.validation.rules.MoreRule;
import me.bottdev.breezeapi.config.validation.rules.RangeRule;
import me.bottdev.breezeapi.config.validation.rules.StructureRule;
import me.bottdev.breezeapi.config.validation.types.RuleConfigValidator;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezeapi.serialization.ObjectNode;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigValidationTest {

    public static SimpleLogger logger = new SimpleLogger("ConfigValidationTest");
    public static JsonMapper jsonMapper = new JsonMapper();

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestConfigurationSection implements Configuration {
        private double doubleValue;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestConfiguration implements Configuration {
        private int number;
        private String requiredString;
        private TestConfigurationSection anotherSection;
    }

    @Test
    void shouldValidate() {

        logger.info("Test validate:");

        String json = """
                {
                    "number": 1,
                    "requiredString": "test",
                    "anotherSection": {
                        "doubleValue": 1.0
                    }
                }
                """;

        Optional<ObjectNode> nodeOptional = jsonMapper.deserializeTree(json);
        if (nodeOptional.isEmpty()) return;
        ObjectNode node = nodeOptional.get();

        RuleConfigValidator validator = new RuleConfigValidator();
        validator.getRuleRegistry()
                .addRootRule(new StructureRule(TestConfiguration.class))
                .addRule(new PathEndsPattern("number"), new MoreRule(0))
                .addRule(new PathEndsPattern("anotherSection.doubleValue"), new RangeRule(0.0, 1.0));

        ValidationResult result = validator.validate(node);
        result.logValidationResult(logger);

        assertEquals(ValidationStatus.SUCCESS, result.getStatus());

    }

    @Test
    void shouldValidateAndLoad() {
        logger.info("Test validate and load:");

        String json = """
                {
                    "number": 1,
                    "requiredString": "test",
                    "anotherSection": {
                        "doubleValue": 1.0
                    }
                }
                """;

        RuleConfigValidator validator = new RuleConfigValidator();
        validator.getRuleRegistry()
                .addRootRule(new StructureRule(TestConfiguration.class))
                .addRule(new PathEndsPattern("number"), new MoreRule(0))
                .addRule(new PathEndsPattern("anotherSection.doubleValue"), new RangeRule(0.0, 1.0));

        ConfigLoader configLoader = new ConfigLoader(jsonMapper, validator);
        Optional<TestConfiguration> configOptional = configLoader.loadConfig(json, TestConfiguration.class);

        assertTrue(configOptional.isPresent());
        TestConfiguration config = configOptional.get();

        logger.info("Loaded config: {}", config);
    }

}
