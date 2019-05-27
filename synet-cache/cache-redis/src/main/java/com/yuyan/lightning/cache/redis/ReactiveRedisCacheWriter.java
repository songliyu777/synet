package com.yuyan.lightning.cache.redis;

import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.time.Duration;

public interface ReactiveRedisCacheWriter {

    Mono<Boolean> put(String name, ByteBuffer key, ByteBuffer value, @Nullable Duration ttl);

    Mono<ByteBuffer> get(String name, ByteBuffer key);

    Mono<Long> remove(String name, ByteBuffer key);

    Mono<Long> clean(String name, ByteBuffer pattern);
}
