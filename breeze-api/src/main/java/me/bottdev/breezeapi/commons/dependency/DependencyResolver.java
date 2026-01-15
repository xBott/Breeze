package me.bottdev.breezeapi.commons.dependency;

import java.util.List;

public interface DependencyResolver<D extends Dependent, C extends DependentContainer<D>> {

    List<D> resolve(C container);

}
