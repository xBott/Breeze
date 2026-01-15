package me.bottdev.breezeprocessor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.commons.dependency.DependentContainer;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ComponentDependencyContainer implements DependentContainer<ComponentDependent> {

    private final List<ComponentDependent> dependents = new ArrayList<>();

    public void add(ComponentDependent componentDependent) {
        dependents.add(componentDependent);
    }

}
