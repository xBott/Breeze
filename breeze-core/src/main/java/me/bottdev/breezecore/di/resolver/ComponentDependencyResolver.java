package me.bottdev.breezecore.di.resolver;

import com.sun.source.tree.Tree;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.dependency.GraphDependencyResolver;
import me.bottdev.breezeapi.index.types.BreezeComponentIndex;
import me.bottdev.breezeapi.log.BreezeLogger;

import java.util.HashMap;

@RequiredArgsConstructor
public class ComponentDependencyResolver implements GraphDependencyResolver<BreezeComponentIndex.Entry, BreezeComponentIndex> {

    @Getter
    private final BreezeLogger logger;

    @Override
    public HashMap<String, Object> createNodeAttributes(BreezeComponentIndex.Entry entry) {
        return new HashMap<>();
    }

}
