package me.bottdev.breezeapi.config.validation;

import me.bottdev.breezeapi.config.validation.patterns.PathEqualsPattern;

import java.util.ArrayList;
import java.util.List;

public class ValidationRuleRegistry {

    private record Entry(FieldPathPattern pattern, ValidationRule rule) {}

    private final List<Entry> entries = new ArrayList<>();

    public ValidationRuleRegistry addRule(FieldPathPattern pattern, ValidationRule rule) {
        entries.add(new Entry(pattern, rule));
        return this;
    }

    public ValidationRuleRegistry addRules(FieldPathPattern pattern, ValidationRule... rules) {
        for (ValidationRule rule : rules) {
            addRule(pattern, rule);
        }
        return this;
    }

    public ValidationRuleRegistry addRootRule(ValidationRule rule) {
        addRule(new PathEqualsPattern(""), rule);
        return this;
    }

    public List<ValidationRule> getRules(String path) {
        return entries.stream()
                .filter(entry -> entry.pattern.matches(path))
                .map(entry -> entry.rule)
                .toList();
    }

}
