package com.yuyan.lightning.cache.interceptor;

import com.yuyan.lightning.cache.ReactiveCache;
import com.yuyan.lightning.cache.ReactiveCacheManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class AbstractCacheResolver implements CacheResolver, InitializingBean {

    @Nullable
    private ReactiveCacheManager cacheManager;


    /**
     * Construct a new {@code AbstractCacheResolver}.
     * @see #setCacheManager
     */
    protected AbstractCacheResolver() {
    }

    /**
     * Construct a new {@code AbstractCacheResolver} for the given {@link CacheManager}.
     * @param cacheManager the CacheManager to use
     */
    protected AbstractCacheResolver(ReactiveCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }


    /**
     * Set the {@link ReactiveCacheManager} that this instance should use.
     */
    public void setCacheManager(ReactiveCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Return the {@link ReactiveCacheManager} that this instance uses.
     */
    public ReactiveCacheManager getCacheManager() {
        Assert.state(this.cacheManager != null, "No CacheManager set");
        return this.cacheManager;
    }

    @Override
    public void afterPropertiesSet()  {
        Assert.notNull(this.cacheManager, "CacheManager is required");
    }


    @Override
    public Collection<? extends ReactiveCache> resolveCaches(CacheOperationInvocationContext<?> context) {
        Collection<String> cacheNames = getCacheNames(context);
        if (cacheNames == null) {
            return Collections.emptyList();
        }
        Collection<ReactiveCache> result = new ArrayList<>(cacheNames.size());
        for (String cacheName : cacheNames) {
            ReactiveCache cache = getCacheManager().getCache(cacheName);
            if (cache == null) {
                throw new IllegalArgumentException("Cannot find cache named '" +
                        cacheName + "' for " + context.getOperation());
            }
            result.add(cache);
        }
        return result;
    }

    /**
     * Provide the name of the cache(s) to resolve against the current cache manager.
     * <p>It is acceptable to return {@code null} to indicate that no cache could
     * be resolved for this invocation.
     * @param context the context of the particular invocation
     * @return the cache name(s) to resolve, or {@code null} if no cache should be resolved
     */
    @Nullable
    protected abstract Collection<String> getCacheNames(CacheOperationInvocationContext<?> context);

}
