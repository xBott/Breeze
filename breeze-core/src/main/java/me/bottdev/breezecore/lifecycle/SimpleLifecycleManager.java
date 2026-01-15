package me.bottdev.breezecore.lifecycle;

import lombok.Getter;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.lifecycle.Lifecycle;
import me.bottdev.breezeapi.lifecycle.LifecycleManager;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.BreezeLoggerFactory;

import java.util.*;

public class SimpleLifecycleManager implements LifecycleManager {

    @Getter
    private final BreezeLogger logger;

    @Inject
    SimpleLifecycleManager(BreezeLoggerFactory loggerFactory) {
        this.logger = loggerFactory.simple("SimpleLifecycleManager");
    }

    @Getter
    private final Map<Class<? extends Lifecycle>, Lifecycle> lifecycles = new HashMap<>();

}
