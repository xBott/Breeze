package me.bottdev.breezeapi.cache;

import java.time.Duration;
import java.util.Optional;

public interface Cache<K, V> {

    void put(K key, V value);

    void put(K key, V value, Duration ttl);

    Optional<V> get(K key);

    void remove(K key);

    void clear();

    int size();

    CacheStats getStats();

}