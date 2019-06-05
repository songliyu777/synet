package com.synet.cache;

import reactor.core.publisher.Mono;

public interface ReactiveCache {

    /**
     * Return the cache name.
     */
    String getName();

    /**
     * Return the underlying native cache provider.
     */
    Object getNativeCache();


    <T> Mono<T> get(Object key, Class<T> type);

    <T> Mono<T> get(Object key, Mono<T> valueLoader);

    /**
     * Associate the specified value with the specified key in this cache.
     * <p>If the cache previously contained a mapping for this key, the old
     * value is replaced by the specified value.
     * @param key the key with which the specified value is to be associated
     * @param value the value to be associated with the specified key
     */
    Mono<Boolean> put(Object key, Object value);

    /**
     * Evict the mapping for this key from this cache if it is present.
     * @param key the key whose mapping is to be removed from the cache
     */
    Mono<Boolean> evict(Object key);

    /**
     * Remove all mappings from the cache.
     */
    Mono<Boolean> clear();
}
