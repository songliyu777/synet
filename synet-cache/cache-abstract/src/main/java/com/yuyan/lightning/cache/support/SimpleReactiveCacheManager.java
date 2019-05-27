package com.yuyan.lightning.cache.support;

import com.yuyan.lightning.cache.ReactiveCache;

import java.util.Collection;
import java.util.Collections;

public class SimpleReactiveCacheManager extends AbstractReactiveCacheManager{

    private Collection<? extends ReactiveCache> caches = Collections.emptySet();

    /**
     * Specify the collection of Cache instances to use for this CacheManager.
     */
    public void setCaches(Collection<? extends ReactiveCache> caches) {
        this.caches = caches;
    }

    @Override
    protected Collection<? extends ReactiveCache> loadCaches() {
        return this.caches;
    }
}
