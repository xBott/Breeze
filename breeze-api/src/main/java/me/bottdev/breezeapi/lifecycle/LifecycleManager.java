package me.bottdev.breezeapi.lifecycle;

import me.bottdev.breezeapi.log.BreezeLogger;

import java.util.Map;
import java.util.Optional;

public interface LifecycleManager {

    BreezeLogger getLogger();
    Map<Class<? extends Lifecycle>, Lifecycle> getLifecycles();

    default boolean exists(Class<? extends Lifecycle> clazz) {
        return getLifecycles().containsKey(clazz);
    }

    default <T extends Lifecycle> T create(LifecycleBuilder<T> builder) {

        T lifecycle = builder.build();

        Class<? extends Lifecycle> clazz = lifecycle.getClass();
        stop(clazz);

        getLifecycles().put(clazz, lifecycle);
        lifecycle.start();

        getLogger().info("Created lifecycle {}", clazz.getSimpleName());

        return lifecycle;
    }

    default void add(Lifecycle lifecycle) {
        getLifecycles().put(lifecycle.getClass(), lifecycle);
    }

    default void remove(Class<? extends Lifecycle> clazz) {
        getLifecycles().remove(clazz);
    }

    @SuppressWarnings("unchecked")
    default  <T extends Lifecycle> Optional<T> get(Class<T> clazz) {

        if (!exists(clazz)) return Optional.empty();

        Lifecycle lifecycle = getLifecycles().get(clazz);
        return Optional.of((T) lifecycle);
    }

    default boolean start(Class<? extends Lifecycle> clazz) {

        Optional<? extends Lifecycle> lifecycleOptional = get(clazz);
        if (lifecycleOptional.isEmpty()) return false;

        Lifecycle lifecycle = lifecycleOptional.get();
        lifecycle.start();
        getLogger().info("Started lifecycle {}", clazz.getSimpleName());

        return true;
    }

    default boolean stop(Class<? extends Lifecycle> clazz) {

        if (!exists(clazz)) return false;

        Lifecycle lifecycle = getLifecycles().remove(clazz);
        lifecycle.shutdown();

        getLogger().info("Stopped lifecycle {}", clazz.getSimpleName());

        return true;
    }

    default void startAll() {
        getLogger().info("Starting all lifecycles");
        getLifecycles().values().forEach(Lifecycle::start);
        getLogger().info("All lifecycles started");
    }

    default void shutdownAll() {
        getLogger().info("Shutting down all lifecycles");
        getLifecycles().values().forEach(Lifecycle::shutdown);
        getLifecycles().clear();
        getLogger().info("All lifecycles stopped");
    }

}
