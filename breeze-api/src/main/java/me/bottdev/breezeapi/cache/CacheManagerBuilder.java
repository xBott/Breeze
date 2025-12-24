package me.bottdev.breezeapi.cache;

import me.bottdev.breezeapi.lifecycle.LifecycleBuilder;

public class CacheManagerBuilder implements LifecycleBuilder<CacheManager> {

    @Override
    public CacheManager build() {
        return new CacheManager();
    }

}
