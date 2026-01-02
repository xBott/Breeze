package me.bottdev.breezecore.di;

import lombok.Getter;
import me.bottdev.breezeapi.lifecycle.Lifecycle;
import me.bottdev.breezeapi.lifecycle.LifecycleManager;
import me.bottdev.breezeapi.log.BreezeLogger;

import java.util.*;

public class LifecycleBreezeContext extends SimpleBreezeContext {

    @Getter
    private final LifecycleManager lifecycleManager;

    public LifecycleBreezeContext(BreezeLogger logger, LifecycleManager lifecycleManager) {
        super(logger);
        this.lifecycleManager = lifecycleManager;
    }

    @Override
    public <T> Optional<T> injectConstructor(Class<T> clazz) {

        Optional<T> injectedObject = super.injectConstructor(clazz);

        injectedObject.ifPresent(object -> {

            if (object instanceof Lifecycle lifecycle) {
                lifecycleManager.add(lifecycle);
            }

        });

        return injectedObject;

    }

}
