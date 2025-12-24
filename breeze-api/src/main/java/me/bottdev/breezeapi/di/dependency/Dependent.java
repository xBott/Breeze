package me.bottdev.breezeapi.di.dependency;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public interface Dependent {

    @JsonIgnore
    String getDependentId();

    List<String> getDependencies();

}
