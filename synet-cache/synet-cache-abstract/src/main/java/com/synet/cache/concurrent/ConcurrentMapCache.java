package com.synet.cache.concurrent;

import com.synet.cache.ReactiveCache;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentMapCache implements ReactiveCache {

    private final String name;

    private final ConcurrentMap<Object, Object> store;

//    private final SerializationDelegate serialization;

    public ConcurrentMapCache(String name) {
        this(name, new ConcurrentHashMap<>(256));
    }

    public ConcurrentMapCache(String name, ConcurrentMap<Object, Object> store) {
        this.name = name;
        this.store = store;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ConcurrentMap<Object, Object> getNativeCache() {
        return this.store;
    }

    @Override
    public <T> Mono<T> get(Object key, Class<T> type) {
        return Mono.defer(() -> {
            Object value = this.store.get(key);
            if (value != null && type != null && !type.isInstance(value)) {
                return Mono.error(new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value));
            }
            return Mono.justOrEmpty((T)value);
        });
    }

    @Override
    public <T> Mono<T> get(Object key, Mono<T> valueLoader) {
        return Mono.defer(() -> {
            Object value = this.store.get(key);
            return Mono.justOrEmpty((T)value).switchIfEmpty(valueLoader);
        });
    }

    @Override
    public Mono<Boolean> put(Object key, Object value) {
        return Mono.defer(() -> {
            this.store.put(key, value);
            return Mono.just(Boolean.TRUE);
        });
    }

    @Override
    public Mono<Boolean> evict(Object key) {
        return Mono.defer(() -> {
            this.store.remove(key);
            return Mono.just(Boolean.TRUE);
        });
    }

    @Override
    public Mono<Boolean> clear() {
        return Mono.defer(() -> {
            this.store.clear();
            return Mono.just(Boolean.TRUE);
        });
    }
}
