package me.bottdev.breezeapi.config.validation;

import lombok.Getter;
import me.bottdev.breezeapi.log.BreezeLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
public class ValidationResult {

    private final List<ValidationNode> nodes = new ArrayList<>();

    public ValidationResult addNode(ValidationNode node) {
        nodes.add(node);
        return this;
    }

    public ValidationStatus getStatus() {
        return getNodes().stream().allMatch(node -> node.getStatus() == ValidationStatus.SUCCESS) ?
                ValidationStatus.SUCCESS :
                ValidationStatus.ERROR;
    }

    public void logValidationResult(BreezeLogger logger) {
        logger.info("Validation status: {}", getStatus().getColored());
        getNodes().forEach(node -> logValidationNode(logger, node));
    }

    private void logValidationNode(BreezeLogger logger, ValidationNode node) {

        String path = node.getPath();
        ValidationStatus status = node.getStatus();

        logger.info("field: {} - {}", path.isEmpty() ? "<root>" : path, status.getColored());

        if (status == ValidationStatus.ERROR) {
            Set<ValidationError> errors = node.getErrors();
            logger.warn("Errors ({}x)", errors.size());
            errors.forEach(error -> {
                logger.warn("- {}", error.getMessage());
            });
        }

        if (!node.hasChildren()) return;
        node.getChildren().forEach(child -> logValidationNode(logger, child));
    }

}
