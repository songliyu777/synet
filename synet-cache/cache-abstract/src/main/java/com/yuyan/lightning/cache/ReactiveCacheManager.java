package com.yuyan.lightning.cache;

import java.util.Collection;

public interface ReactiveCacheManager {

    /**
     * Return the cache associated with the given name.
     * @param name the cache identifier (must not be {@code null})
     * @return the associated cache, or {@code null} if none found
     */
    ReactiveCache getCache(String name);

    /**
     * Return a collection of the cache names known by this manager.
     * @return the names of all caches known by the cache manager
     */
    Collection<String> getCacheNames();
}
