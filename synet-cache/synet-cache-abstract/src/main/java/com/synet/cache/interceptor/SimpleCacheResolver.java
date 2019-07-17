package com.synet.cache.interceptor;

import com.synet.cache.ReactiveCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;

import java.util.Collection;

public class SimpleCacheResolver extends AbstractCacheResolver{

    public SimpleCacheResolver() {
    }

    public SimpleCacheResolver(ReactiveCacheManager cacheManager) {
        super(cacheManager);
    }

    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        return context.getOperation().getCacheNames();
    }


    /**
     * Return a {@code SimpleCacheResolver} for the given {@link CacheManager}.
     * @param cacheManager the CacheManager (potentially {@code null})
     * @return the SimpleCacheResolver ({@code null} if the CacheManager was {@code null})
     * @since 5.1
     */
    @Nullable
    static SimpleCacheResolver of(@Nullable ReactiveCacheManager cacheManager) {
        return (cacheManager != null ? new SimpleCacheResolver(cacheManager) : null);
    }
}
