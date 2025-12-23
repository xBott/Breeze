package me.bottdev.breezeapi.lifecycle;

import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;

import java.util.*;

public class LifecycleManager {

    private final BreezeLogger logger = new SimpleLogger("LifecycleManager");

    private final Map<Class<? extends Lifecycle>, Lifecycle> lifecycles = new HashMap<>();

    public boolean exists(Class<? extends Lifecycle> clazz) {
        return lifecycles.containsKey(clazz);
    }

    public <T extends Lifecycle> T create(LifecycleBuilder<T> builder) {

        T lifecycle = builder.build();

        Class<? extends Lifecycle> clazz = lifecycle.getClass();
        stop(clazz);

        lifecycles.put(clazz, lifecycle);
        lifecycle.start();

        logger.info("Created lifecycle {}", clazz.getSimpleName());

        return lifecycle;
    }

    @SuppressWarnings("unchecked")
    public <T extends Lifecycle> Optional<T> get(Class<T> clazz) {

        if (!exists(clazz)) return Optional.empty();

        Lifecycle lifecycle = lifecycles.get(clazz);
        return Optional.of((T) lifecycle);
    }

    public boolean stop(Class<? extends Lifecycle> clazz) {

        if (!exists(clazz)) return false;

        Lifecycle lifecycle = lifecycles.remove(clazz);
        lifecycle.shutdown();

        logger.info("Stopped lifecycle {}", clazz.getSimpleName());

        return true;
    }

    public void startAll() {
        logger.info("Starting all lifecycles");
        lifecycles.values().forEach(Lifecycle::start);
        logger.info("All lifecycles started");
    }

    public void shutdownAll() {
        logger.info("Shutting down all lifecycles");
        lifecycles.values().forEach(Lifecycle::shutdown);
        lifecycles.clear();
        logger.info("All lifecycles stopped");
    }

}
