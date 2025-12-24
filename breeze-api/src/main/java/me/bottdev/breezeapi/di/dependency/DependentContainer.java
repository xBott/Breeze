package me.bottdev.breezeapi.di.dependency;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface DependentContainer<D extends Dependent> {

    List<D> getDependents();

    @JsonIgnore
    default Map<String, D> getDependentWithIds() {
        HashMap<String, D> results = new HashMap<>();
        getDependents().forEach(dependent -> results.put(dependent.getDependentId(), dependent));
        return results;
    }

}
