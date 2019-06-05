package com.synet.cache.interceptor;

import com.synet.cache.ReactiveCache;

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
