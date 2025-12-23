package me.bottdev.breezeapi.cache;

import lombok.Getter;
import me.bottdev.breezeapi.lifecycle.Lifecycle;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LRUCache<K, V> extends Lifecycle implements Cache<K, V> {

    @Getter
    private final int maxSize;
    @Getter
    private final Map<K, CacheEntry<V>> data;
    private final CacheStats stats;
    private final ReadWriteLock lock;
    private final ScheduledExecutorService cleanupExecutor;
    
    public LRUCache(int maxSize) {
        this.maxSize = maxSize;
        this.data = Collections.synchronizedMap(
                new LinkedHashMap<>(maxSize + 1, 0.75f, true) {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                        return size() > maxSize;
                    }
                }
        );
        this.stats = new CacheStats();
        this.lock = new ReentrantReadWriteLock();
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
        
        cleanupExecutor.scheduleAtFixedRate(this::cleanExpired, 1, 1, TimeUnit.MINUTES);
    }
    
    @Override
    public void put(K key, V value) {
        put(key, value, null);
    }
    
    @Override
    public void put(K key, V value, Duration ttl) {
        lock.writeLock().lock();
        try {
            data.put(key, new CacheEntry<>(value, ttl));
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public Optional<V> get(K key) {
        lock.readLock().lock();
        try {
            CacheEntry<V> entry = data.get(key);
            
            if (entry == null) {
                stats.recordMiss();
                return Optional.empty();
            }
            
            if (entry.isExpired()) {
                stats.recordMiss();
                data.remove(key);
                return Optional.empty();
            }
            
            stats.recordHit();
            return Optional.of(entry.getValue());

        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void remove(K key) {
        lock.writeLock().lock();
        try {
            data.remove(key);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            data.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public int size() {
        return data.size();
    }
    
    @Override
    public CacheStats getStats() {
        return stats;
    }
    
    private void cleanExpired() {
        lock.writeLock().lock();
        try {
            data.entrySet().removeIf(entry ->
                    entry.getValue().isExpired()
            );
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onShutdown() {
        cleanupExecutor.shutdown();
    }

}