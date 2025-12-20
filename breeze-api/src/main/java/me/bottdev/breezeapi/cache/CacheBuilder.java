package me.bottdev.breezeapi.cache;

public class CacheBuilder<K, V> {
    private int maxSize = 1000;

    public CacheBuilder<K, V> maxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }
    
    public Cache<K, V> build() {
        return new LRUCache<>(maxSize);
    }

}