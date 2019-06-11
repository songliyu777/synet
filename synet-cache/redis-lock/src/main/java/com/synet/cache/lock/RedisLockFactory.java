package com.synet.cache.lock;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

public class RedisLockFactory {

    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    public RedisLockFactory(ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
    }

    public RedisLock NewLock(Long expire) {
        return new RedisLock(expire, reactiveStringRedisTemplate);
    }

}
