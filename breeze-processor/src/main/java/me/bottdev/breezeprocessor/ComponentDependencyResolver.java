package me.bottdev.breezeprocessor;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.commons.dependency.GraphDependencyResolver;

import java.util.HashMap;

@RequiredArgsConstructor
public class ComponentDependencyResolver implements GraphDependencyResolver<ComponentDependent, ComponentDependencyContainer> {

    @Override
    public HashMap<String, Object> createNodeAttributes(ComponentDependent entry) {
        return new HashMap<>();
    }

}
