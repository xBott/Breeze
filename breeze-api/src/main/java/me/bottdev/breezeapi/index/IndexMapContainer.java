package me.bottdev.breezeapi.index;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import me.bottdev.breezeapi.commons.dependency.DependentContainer;

import java.util.List;

@Builder
@Getter
public class IndexMapContainer implements DependentContainer<IndexMap> {

    @Singular("dependency")
    private List<IndexMap> dependents;

}
