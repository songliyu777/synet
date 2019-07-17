package com.synet.cache.autoconfigure;

import com.synet.cache.ReactiveCacheManager;

@FunctionalInterface
public interface ReactiveCacheManagerCustomizer<T extends ReactiveCacheManager> {

    /**
     * Customize the cache manager.
     * @param cacheManager the {@code CacheManager} to customize
     */
    void customize(T cacheManager);

}
