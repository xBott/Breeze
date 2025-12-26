package me.bottdev.breezeapi.config.validation.types;

import lombok.Getter;
import me.bottdev.breezeapi.config.validation.*;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezeapi.serialization.ObjectNode;

import java.util.List;

public class RuleConfigValidator implements ConfigValidator {

    private final BreezeLogger logger = new SimpleLogger("RuleConfigValidator");

    @Getter
    private final ValidationRuleRegistry ruleRegistry = new ValidationRuleRegistry();

    @Override
    public ValidationResult validate(ObjectNode root) {
        ValidationResult result = new ValidationResult();

        ValidationNode rootNode = validateNode(new ValidationContext(root, "", ""));
        result.addNode(rootNode);

        for (ObjectNode child : root.getChildren()) {
            walkNode(result, child, "");
        }

        return result;
    }

    private void walkNode(ValidationResult result, ObjectNode node, String path) {

        String fieldName = node.getName();
        String currentPath = path.isEmpty()
                ? fieldName
                : path + "." + fieldName;

        ValidationContext context = new ValidationContext(node, currentPath, fieldName);
        ValidationNode validationNode = validateNode(context);

        result.addNode(validationNode);

        if (!node.hasChildren()) return;

        for (ObjectNode child : node.getChildren()) {
            walkNode(result, child, currentPath);
        }
    }

    private ValidationNode validateNode(ValidationContext context) {
        String currentPath = context.getPath();
        List<ValidationRule> rules = ruleRegistry.getRules(currentPath);
        ValidationNode validationNode = new ValidationNode(currentPath, ValidationStatus.SUCCESS);
        rules.forEach(rule -> rule.validate(context, validationNode));
        return validationNode;
    }

}
