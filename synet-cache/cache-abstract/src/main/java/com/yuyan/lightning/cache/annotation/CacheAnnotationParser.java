package com.yuyan.lightning.cache.annotation;

import com.yuyan.lightning.cache.interceptor.CacheOperation;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Collection;

public interface CacheAnnotationParser {

    @Nullable
    Collection<CacheOperation> parseCacheAnnotations(Class<?> type);

    @Nullable
    Collection<CacheOperation> parseCacheAnnotations(Method method);
}
