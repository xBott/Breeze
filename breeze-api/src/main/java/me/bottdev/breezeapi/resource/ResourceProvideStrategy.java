package me.bottdev.breezeapi.resource;

import java.lang.reflect.Method;
import java.util.Optional;

public interface ResourceProvideStrategy {

    ResourceChunkContainer provide(Method method);

}
