package me.bottdev.breezecore.di.resolver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.dependency.GraphDependencyResolver;
import me.bottdev.breezeapi.index.BreezeIndexBucket;
import me.bottdev.breezeapi.index.BreezeIndexBucketContainer;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleLogger;
import me.bottdev.breezeapi.log.TreeLogger;

import java.util.HashMap;

@RequiredArgsConstructor
public class IndexBucketDependencyResolver implements GraphDependencyResolver<BreezeIndexBucket, BreezeIndexBucketContainer> {

    @Getter
    private final TreeLogger logger;

    @Override
    public HashMap<String, Object> createNodeAttributes(BreezeIndexBucket entry) {
        return new HashMap<>();
    }

}
