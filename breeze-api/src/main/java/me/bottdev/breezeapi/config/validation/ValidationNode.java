package me.bottdev.breezeapi.config.validation;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class ValidationNode {


    private final String path;
    @Setter
    private ValidationStatus status;
    private final Set<ValidationError> errors = new HashSet<>();
    private final List<ValidationNode> children =  new ArrayList<>();

    public ValidationNode(String path, ValidationStatus status) {
        this.path = path;
        this.status = status;
    }

    public ValidationNode addError(ValidationError error) {
        errors.add(error);
        return this;
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

}
