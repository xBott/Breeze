package me.bottdev.breezeapi.cache;

import me.bottdev.breezeapi.commons.Lifecycle;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager implements Lifecycle {

    private final Map<String, Cache<?, ?>> caches = new ConcurrentHashMap<>();

    public boolean contains(String name) {
        return caches.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    public <K, V> Optional<Cache<K, V>> get(String name) {
        Cache<?, ?> cache = caches.get(name);
        return Optional.ofNullable((Cache<K, V>) cache);
    }

    public <K, V> Cache<K, V> create(
            String name,
            CacheBuilder<K, V> builder
    ) {
        if (name == null || builder == null) {
            throw new IllegalArgumentException("Name and builder cannot be null");
        }

        if (caches.containsKey(name)) {
            throw new IllegalStateException("Cache already exists: " + name);
        }

        Cache<K, V> cache = builder.build();
        caches.put(name, cache);
        return cache;
    }

    @SuppressWarnings("unchecked")
    public <K, V> Cache<K, V> getOrCreate(String name, CacheBuilder<K, V> builder) {
        return (Cache<K, V>) caches.computeIfAbsent(name, key -> builder.build());
    }

    @SuppressWarnings("unchecked")
    public <K, V> Optional<Cache<K, V>> remove(String name) {
        Cache<?, ?> removed = caches.remove(name);

        if (removed instanceof Lifecycle lifecycle) {
            lifecycle.shutdown();
        }

        return Optional.ofNullable((Cache<K, V>) removed);
    }

    public void clear() {
        caches.values().forEach(cache -> {
            if (cache instanceof Lifecycle lifecycle) {
                lifecycle.shutdown();
            }
        });
        caches.clear();
    }

    public Set<String> getCacheNames() {
        return Collections.unmodifiableSet(caches.keySet());
    }

    @Override
    public void shutdown() {
        clear();
    }
}