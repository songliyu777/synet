package com.yuyan.lightning.cache.redis;

import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DefaultReactiveRedisCacheWriter implements ReactiveRedisCacheWriter {

    private final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;
    private final Duration sleepTime;

    public DefaultReactiveRedisCacheWriter(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        this(reactiveRedisConnectionFactory, Duration.ZERO);
    }

    public DefaultReactiveRedisCacheWriter(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory, Duration sleepTime) {
        Assert.notNull(reactiveRedisConnectionFactory, "ConnectionFactory must not be null!");
        Assert.notNull(sleepTime, "SleepTime must not be null!");

        this.reactiveRedisConnectionFactory = reactiveRedisConnectionFactory;
        this.sleepTime = sleepTime;
    }

    @Override
    public Mono<Boolean> put(String name, ByteBuffer key, ByteBuffer value, Duration ttl) {
        ReactiveRedisConnection reactiveConnection = reactiveRedisConnectionFactory.getReactiveConnection();
        if (shouldExpireWithin(ttl)) {
            return reactiveConnection.stringCommands().set(key, value, Expiration.from(ttl.toMillis(), TimeUnit.MILLISECONDS), RedisStringCommands.SetOption.upsert());
        }
        return reactiveConnection.stringCommands().set(key, value);
    }

    @Override
    public Mono<ByteBuffer> get(String name, ByteBuffer key) {
        ReactiveRedisConnection reactiveConnection = reactiveRedisConnectionFactory.getReactiveConnection();
        return reactiveConnection.stringCommands().get(key);
    }

    @Override
    public Mono<Long> remove(String name, ByteBuffer key) {
        ReactiveRedisConnection reactiveConnection = reactiveRedisConnectionFactory.getReactiveConnection();
        return reactiveConnection.keyCommands().del(key);
    }

    @Override
    public Mono<Long> clean(String name, ByteBuffer pattern) {
        ReactiveRedisConnection reactiveConnection = reactiveRedisConnectionFactory.getReactiveConnection();
        Mono<List<ByteBuffer>> keys = reactiveConnection.keyCommands().keys(pattern);
        return keys.flatMap(byteBuffers -> CollectionUtils.isEmpty(byteBuffers) ? Mono.just(0L) : reactiveConnection.keyCommands().mDel(byteBuffers));
    }

    private static boolean shouldExpireWithin(@Nullable Duration ttl) {
        return ttl != null && !ttl.isZero() && !ttl.isNegative();
    }
}
