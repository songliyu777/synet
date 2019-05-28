package com.yuyan.lightning.cache.interceptor;

import com.yuyan.lightning.cache.ReactiveCacheManager;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class NamedCacheResolver extends AbstractCacheResolver{

    @Nullable
    private Collection<String> cacheNames;

    public NamedCacheResolver() {
    }

    public NamedCacheResolver(ReactiveCacheManager cacheManager, String... cacheNames) {
        super(cacheManager);
        this.cacheNames = new ArrayList<>(Arrays.asList(cacheNames));
    }

    /**
     * Set the cache name(s) that this resolver should use.
     */
    public void setCacheNames(Collection<String> cacheNames) {
        this.cacheNames = cacheNames;
    }

    @Override
    protected Collection<String> getCacheNames(CacheOperationInvocationContext<?> context) {
        return this.cacheNames;
    }


}
