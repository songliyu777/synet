package com.synet.cache;

import com.synet.cache.support.SimpleReactiveCacheManager;
import org.junit.Test;
import org.springframework.util.Assert;

public class SimpleCacheManagerTests {

    private SimpleReactiveCacheManager emptyCacheManager() {
        SimpleReactiveCacheManager cacheManager = new SimpleReactiveCacheManager();
        cacheManager.afterPropertiesSet();
        return cacheManager;
    }

    @Test
    public void testCreateSimpleReactiveCacheManager() {
        SimpleReactiveCacheManager cacheManager = emptyCacheManager();
    }


    @Test
    public void testGetCacheFromEmptyManager() {
        SimpleReactiveCacheManager cacheManager = emptyCacheManager();
        String cacheName = "test";
        ReactiveCache cache = cacheManager.getCache(cacheName);
        Assert.isNull(cache, "ReactiveCache is not null");
    }
}
