package com.synet.cache;

import com.synet.cache.ReactiveCache;
import com.synet.cache.redis.DefaultReactiveRedisCacheWriter;
import com.synet.cache.redis.ReactiveRedisCacheManager;
import com.synet.cache.redis.ReactiveRedisCacheWriter;
import org.junit.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ReactiveRedisCacheManagerTests {

    private final static String HOST = "192.168.99.108";

    private ReactiveRedisCacheManager emptyCacheManager() {
        //connection factory
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(HOST);
        LettuceConnectionFactory reactiveRedisConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        reactiveRedisConnectionFactory.afterPropertiesSet();

        //cache manager
        ReactiveRedisCacheManager.RedisCacheManagerBuilder builder = ReactiveRedisCacheManager.builder(reactiveRedisConnectionFactory);
        ReactiveRedisCacheManager redisCacheManager = builder.build();
        redisCacheManager.afterPropertiesSet();
        return redisCacheManager;
    }

    @Test
    public void testCreateReactiveRedisCacheManagerFromConnectionFactory() {
        //connection factory
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(HOST);
        LettuceConnectionFactory reactiveRedisConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        reactiveRedisConnectionFactory.afterPropertiesSet();

        //cache manager
        ReactiveRedisCacheManager.RedisCacheManagerBuilder builder = ReactiveRedisCacheManager.builder(reactiveRedisConnectionFactory);
        ReactiveRedisCacheManager redisCacheManager = builder.build();
        redisCacheManager.afterPropertiesSet();

        //
        Assert.notNull(redisCacheManager, "redisCacheManager is null");
    }

    @Test
    public void testCreateReactiveRedisCacheManagerFromCacheWriter() {
        //connection factory
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(HOST);
        LettuceConnectionFactory reactiveRedisConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        reactiveRedisConnectionFactory.afterPropertiesSet();

        //cache writer
        ReactiveRedisCacheWriter cacheWriter = new DefaultReactiveRedisCacheWriter(reactiveRedisConnectionFactory);

        //cache manager
        ReactiveRedisCacheManager.RedisCacheManagerBuilder builder = ReactiveRedisCacheManager.builder(cacheWriter);
        ReactiveRedisCacheManager redisCacheManager = builder.build();
        redisCacheManager.afterPropertiesSet();


        Assert.notNull(redisCacheManager, "redisCacheManager is null");
    }

    @Test
    public void testGetCacheFromEmptyCacheManager() {
        //if missing cache , manager will auto put a new Cache into manager
        ReactiveRedisCacheManager cacheManager = emptyCacheManager();
        ReactiveCache reactiveCache = cacheManager.getCache("test");
        Assert.notNull(reactiveCache, "reactiveCache is null");
    }

    @Test
    public void testGetContentFromCacheFromEmptyCacheManager() {
        ReactiveRedisCacheManager cacheManager = emptyCacheManager();
        ReactiveCache cache = cacheManager.getCache("test");

        //put value into cache
        String key = "test";
        String value = "testGetContentFromCacheFromEmptyCacheManager";
        Mono<Boolean> put = cache.put(key, value);
        StepVerifier.create(put)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        Mono<String> get = cache.get(key, String.class);
        StepVerifier.create(get)
                .consumeNextWith(System.out::println)
                .verifyComplete();
    }
}
