package com.yuyan.lightning.cache.interceptor;

import com.yuyan.lightning.cache.ReactiveCache;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

public abstract class AbstractCacheInvoker {

    protected <T> Mono<T> doGet(ReactiveCache cache, Object key, Mono<T> valueLoader) {
        return cache.get(key, valueLoader);
    }

    protected Mono<Boolean> doPut(ReactiveCache cache, Object key, @Nullable Object result) {
        return cache.put(key, result)
                .doOnError(Mono::just);
    }

    protected Mono<Boolean> doEvict(ReactiveCache cache, Object key) {
        return cache.evict(key)
                .doOnError(Mono::just);
    }

    protected Mono<Boolean> doClear(ReactiveCache cache) {
        return cache.clear()
                .doOnError(Mono::just);
    }
}
