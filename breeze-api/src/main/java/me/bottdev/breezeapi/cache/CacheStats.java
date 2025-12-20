package me.bottdev.breezeapi.cache;

import lombok.Getter;

@Getter
public class CacheStats {

    private long hits = 0;
    private long misses = 0;
    
    public void recordHit() { hits++; }
    public void recordMiss() { misses++; }

    public double getHitRate() {
        long total = hits + misses;
        return total == 0 ? 0.0 : (double) hits / total;
    }
    
    @Override
    public String toString() {
        return String.format("Hits: %d, Misses: %d, Hit Rate: %.2f%%", 
            hits, misses, getHitRate() * 100);
    }

}