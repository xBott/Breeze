package me.bottdev.breezecore.di.resolver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.commons.dependency.GraphDependencyResolver;
import me.bottdev.breezeapi.index.IndexMap;
import me.bottdev.breezeapi.index.IndexMapContainer;
import me.bottdev.breezeapi.log.BreezeLogger;

import java.util.HashMap;

@RequiredArgsConstructor
public class IndexMapDependencyResolver implements GraphDependencyResolver<IndexMap, IndexMapContainer> {

    @Getter
    private final BreezeLogger logger;

    @Override
    public HashMap<String, Object> createNodeAttributes(IndexMap entry) {
        return new HashMap<>();
    }

}
