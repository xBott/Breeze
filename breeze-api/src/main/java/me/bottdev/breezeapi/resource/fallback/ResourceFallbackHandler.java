package me.bottdev.breezeapi.resource.fallback;

import java.lang.reflect.Method;
import java.util.Optional;

public interface ResourceFallbackHandler<T> {

    Optional<T> handle(Method method);

}
