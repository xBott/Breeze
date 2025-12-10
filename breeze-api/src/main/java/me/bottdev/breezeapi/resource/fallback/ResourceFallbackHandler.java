package me.bottdev.breezeapi.resource.fallback;

import java.lang.reflect.Method;

public interface ResourceFallbackHandler {

    Object fallback(Method method);

}
