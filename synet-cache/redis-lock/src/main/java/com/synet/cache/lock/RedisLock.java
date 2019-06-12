package com.synet.cache.lock;


import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 做分布式锁，利用 redis的setnx（SET if Not eXists）命令 和 getset 命令，即下面代码中的方法 setIfAbsent 和 getAndSet
 * 线程安全随机数作为种子，设置过期时间
 */
public class RedisLock {

    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    String value;

    protected RedisLock(Long expire, ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
        Long time = System.currentTimeMillis() + expire;
        value = ThreadLocalRandom.current().nextInt() + "&" + time.toString();
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
    }

    /**
     * 加锁
     *
     * @param key 锁的key
     * @return
     */
    public Mono<Boolean> lock(String key) {
        //如果key值不存在，则返回 true，且设置 value
        Mono<String> m1 = reactiveStringRedisTemplate.opsForValue().setIfAbsent(key, value).flatMap(b -> {
            if (b) {
                return Mono.just(value);
            }
            return reactiveStringRedisTemplate.opsForValue().get(key);
        });
        //如果key存在，则比较是否和当前值一样，当前值一样的说明加锁成功,超时就重新设置锁，不一样的说明正在占用
        Mono<Boolean> m2 = m1.switchIfEmpty(Mono.just(value)).flatMap(s -> {
            if (s.equals(value) || Long.parseLong(s.split("&")[1]) < System.currentTimeMillis()) {
                return reactiveStringRedisTemplate.opsForValue().getAndSet(key, value).then(Mono.just(Boolean.TRUE));
            }
            return Mono.just(Boolean.FALSE);
        });
        return m2;
    }

    /**
     * 解锁
     *
     * @param key
     */
    public Mono<Boolean> unlock(String key) {
        return reactiveStringRedisTemplate.opsForValue().get(key)
                .switchIfEmpty(Mono.just(value))
                .flatMap(s -> {
                    if (s.equals(value)) {
                        return reactiveStringRedisTemplate.opsForValue().delete(key);
                    }
                    return Mono.just(Boolean.FALSE);
                });
    }
}