package com.yuyan.lightning.cache.interceptor;

import com.yuyan.lightning.cache.ReactiveCache;

import java.util.Collection;

@FunctionalInterface
public interface CacheResolver {

    /**
     * Return the cache(s) to use for the specified invocation.
     * @param context the context of the particular invocation
     * @return the cache(s) to use (never {@code null})
     * @throws IllegalStateException if cache resolution failed
     */
    Collection<? extends ReactiveCache> resolveCaches(CacheOperationInvocationContext<?> context);

}
