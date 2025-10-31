package me.bottdev.breezecore.di.resolver;

import lombok.Getter;
import me.bottdev.breezeapi.dependency.GraphDependencyResolver;
import me.bottdev.breezeapi.index.BreezeIndexBucket;
import me.bottdev.breezeapi.index.BreezeIndexBucketContainer;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleLogger;

import java.util.HashMap;

public class IndexBucketDependencyResolver implements GraphDependencyResolver<BreezeIndexBucket, BreezeIndexBucketContainer> {

    @Getter
    private final BreezeLogger logger = new SimpleLogger("BreezeBucketDependencyResolver");

    @Override
    public HashMap<String, Object> createNodeAttributes(BreezeIndexBucket entry) {
        return new HashMap<>();
    }

}
