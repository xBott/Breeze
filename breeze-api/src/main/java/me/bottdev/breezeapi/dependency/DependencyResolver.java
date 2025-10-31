package me.bottdev.breezeapi.dependency;

import me.bottdev.breezeapi.log.BreezeLogger;

import java.util.List;

public interface DependencyResolver<D extends Dependent, C extends DependentContainer<D>> {

    BreezeLogger getLogger();

    List<D> resolve(C container);

}
