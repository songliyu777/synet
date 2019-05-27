package com.yuyan.lightning.cache.interceptor;

import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Collection;

public interface CacheOperationSource {

    /**
     * Return the collection of cache operations for this method,
     * or {@code null} if the method contains no <em>cacheable</em> annotations.
     * @param method the method to introspect
     * @param targetClass the target class (may be {@code null}, in which case
     * the declaring class of the method must be used)
     * @return all cache operations for this method, or {@code null} if none found
     */
    @Nullable
    Collection<CacheOperation> getCacheOperations(Method method, @Nullable Class<?> targetClass);
}
