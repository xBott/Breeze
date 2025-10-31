package me.bottdev.breezecore.di.resolver;

import lombok.Getter;
import me.bottdev.breezeapi.dependency.GraphDependencyResolver;
import me.bottdev.breezeapi.index.types.BreezeComponentIndex;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleLogger;

import java.util.HashMap;

public class ComponentDependencyResolver implements GraphDependencyResolver<BreezeComponentIndex.Entry, BreezeComponentIndex> {

    @Getter
    private final BreezeLogger logger = new SimpleLogger("ComponentDependencyResolver");

    @Override
    public HashMap<String, Object> createNodeAttributes(BreezeComponentIndex.Entry entry) {
        return new HashMap<>();
    }

}
