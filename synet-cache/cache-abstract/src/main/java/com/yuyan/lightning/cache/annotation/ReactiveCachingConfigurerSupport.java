package com.yuyan.lightning.cache.annotation;

import com.yuyan.lightning.cache.ReactiveCacheManager;
import com.yuyan.lightning.cache.interceptor.CacheResolver;
import com.yuyan.lightning.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.lang.Nullable;

public class ReactiveCachingConfigurerSupport implements ReactiveCachingConfigurer{

    @Override
    @Nullable
    public ReactiveCacheManager cacheManager() {
        return null;
    }

    @Override
    @Nullable
    public CacheResolver cacheResolver() {
        return null;
    }

    @Override
    @Nullable
    public KeyGenerator keyGenerator() {
        return null;
    }

    @Override
    @Nullable
    public CacheErrorHandler errorHandler() {
        return null;
    }
}
