package com.yuyan.lightning.autoconfigure.cache;

import com.yuyan.lightning.cache.ReactiveCacheManager;

@FunctionalInterface
public interface ReactiveCacheManagerCustomizer<T extends ReactiveCacheManager> {

    /**
     * Customize the cache manager.
     * @param cacheManager the {@code CacheManager} to customize
     */
    void customize(T cacheManager);

}
