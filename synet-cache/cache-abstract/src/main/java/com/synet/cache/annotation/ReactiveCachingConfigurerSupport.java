package com.synet.cache.annotation;

import com.synet.cache.interceptor.CacheResolver;
import com.synet.cache.interceptor.KeyGenerator;
import com.synet.cache.ReactiveCacheManager;
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
