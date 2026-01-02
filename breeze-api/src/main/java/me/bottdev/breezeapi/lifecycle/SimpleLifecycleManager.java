package me.bottdev.breezeapi.lifecycle;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.log.BreezeLogger;

import java.util.*;

@RequiredArgsConstructor
public class SimpleLifecycleManager implements LifecycleManager {

    @Getter
    private final BreezeLogger logger;

    @Getter
    private final Map<Class<? extends Lifecycle>, Lifecycle> lifecycles = new HashMap<>();

}
