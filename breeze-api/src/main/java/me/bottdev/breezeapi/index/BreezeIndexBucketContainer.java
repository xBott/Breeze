package me.bottdev.breezeapi.index;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import me.bottdev.breezeapi.dependency.DependentContainer;

import java.util.List;

@Builder
@Getter
public class BreezeIndexBucketContainer implements DependentContainer<BreezeIndexBucket> {

    @Singular("dependency")
    private List<BreezeIndexBucket> dependents;

}
