package me.bottdev.breezeapi.cache;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;

public class CacheEntry<V> {
    private final V value;
    private final Instant expiryTime;
    @Getter
    private Instant lastAccessTime;
    @Getter
    private int accessCount;
    
    public CacheEntry(V value, Duration ttl) {
        this.value = value;
        this.expiryTime = ttl == null ? null : Instant.now().plus(ttl);
        this.lastAccessTime = Instant.now();
        this.accessCount = 0;
    }
    
    public V getValue() {
        lastAccessTime = Instant.now();
        accessCount++;
        return value;
    }
    
    public boolean isExpired() {
        return expiryTime != null && Instant.now().isAfter(expiryTime);
    }

}