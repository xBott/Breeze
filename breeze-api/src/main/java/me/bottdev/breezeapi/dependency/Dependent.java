package me.bottdev.breezeapi.dependency;

import java.util.List;

public interface Dependent {

    String getDependentId();

    List<String> getDependencies();

}
