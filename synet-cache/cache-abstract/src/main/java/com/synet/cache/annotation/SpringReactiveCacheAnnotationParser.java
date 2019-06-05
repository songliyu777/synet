package com.synet.cache.annotation;

import com.synet.cache.interceptor.CacheEvictOperation;
import com.synet.cache.interceptor.CachePutOperation;
import com.synet.cache.interceptor.CacheableOperation;
import com.synet.cache.interceptor.CacheOperation;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class SpringReactiveCacheAnnotationParser implements CacheAnnotationParser, Serializable {

    private static final Set<Class<? extends Annotation>> CACHE_OPERATION_ANNOTATIONS = new LinkedHashSet<>(8);

    static {
        CACHE_OPERATION_ANNOTATIONS.add(ReactiveCacheable.class);
        CACHE_OPERATION_ANNOTATIONS.add(ReactiveCacheEvict.class);
        CACHE_OPERATION_ANNOTATIONS.add(ReactiveCachePut.class);
        CACHE_OPERATION_ANNOTATIONS.add(ReactiveCaching.class);
    }


//    @Override
//    public boolean isCandidateClass(Class<?> targetClass) {
//        return AnnotationUtils.isCandidateClass(targetClass, CACHE_OPERATION_ANNOTATIONS);
//    }

    @Override
    @Nullable
    public Collection<CacheOperation> parseCacheAnnotations(Class<?> type) {
        DefaultCacheConfig defaultConfig = new DefaultCacheConfig(type);
        return parseCacheAnnotations(defaultConfig, type);
    }

    @Override
    @Nullable
    public Collection<CacheOperation> parseCacheAnnotations(Method method) {
        DefaultCacheConfig defaultConfig = new DefaultCacheConfig(method.getDeclaringClass());
        return parseCacheAnnotations(defaultConfig, method);
    }

    @Nullable
    private Collection<CacheOperation> parseCacheAnnotations(DefaultCacheConfig cachingConfig, AnnotatedElement ae) {
        Collection<CacheOperation> ops = parseCacheAnnotations(cachingConfig, ae, false);
        if (ops != null && ops.size() > 1) {
            // More than one operation found -> local declarations override interface-declared ones...
            Collection<CacheOperation> localOps = parseCacheAnnotations(cachingConfig, ae, true);
            if (localOps != null) {
                return localOps;
            }
        }
        return ops;
    }

    @Nullable
    private Collection<CacheOperation> parseCacheAnnotations(
            DefaultCacheConfig cachingConfig, AnnotatedElement ae, boolean localOnly) {

        Collection<? extends Annotation> anns = (localOnly ?
                AnnotatedElementUtils.getAllMergedAnnotations(ae, CACHE_OPERATION_ANNOTATIONS) :
                AnnotatedElementUtils.findAllMergedAnnotations(ae, CACHE_OPERATION_ANNOTATIONS));
        if (anns.isEmpty()) {
            return null;
        }

        final Collection<CacheOperation> ops = new ArrayList<>(1);
        anns.stream().filter(ann -> ann instanceof ReactiveCacheable).forEach(
                ann -> ops.add(parseCacheableAnnotation(ae, cachingConfig, (ReactiveCacheable) ann)));
        anns.stream().filter(ann -> ann instanceof ReactiveCacheEvict).forEach(
                ann -> ops.add(parseEvictAnnotation(ae, cachingConfig, (ReactiveCacheEvict) ann)));
        anns.stream().filter(ann -> ann instanceof ReactiveCachePut).forEach(
                ann -> ops.add(parsePutAnnotation(ae, cachingConfig, (ReactiveCachePut) ann)));
        anns.stream().filter(ann -> ann instanceof ReactiveCaching).forEach(
                ann -> parseCachingAnnotation(ae, cachingConfig, (ReactiveCaching) ann, ops));
        return ops;
    }

    private CacheableOperation parseCacheableAnnotation(
            AnnotatedElement ae, DefaultCacheConfig defaultConfig, ReactiveCacheable cacheable) {

        CacheableOperation.Builder builder = new CacheableOperation.Builder();

        builder.setName(ae.toString());
        builder.setCacheNames(cacheable.cacheNames());
        builder.setCondition(cacheable.condition());
        builder.setUnless(cacheable.unless());
        builder.setKey(cacheable.key());
        builder.setKeyGenerator(cacheable.keyGenerator());
        builder.setCacheManager(cacheable.cacheManager());
        builder.setCacheResolver(cacheable.cacheResolver());
        builder.setSync(cacheable.sync());

        defaultConfig.applyDefault(builder);
        CacheableOperation op = builder.build();
        validateCacheOperation(ae, op);

        return op;
    }

    private CacheEvictOperation parseEvictAnnotation(
            AnnotatedElement ae, DefaultCacheConfig defaultConfig, ReactiveCacheEvict cacheEvict) {

        CacheEvictOperation.Builder builder = new CacheEvictOperation.Builder();

        builder.setName(ae.toString());
        builder.setCacheNames(cacheEvict.cacheNames());
        builder.setCondition(cacheEvict.condition());
        builder.setKey(cacheEvict.key());
        builder.setKeyGenerator(cacheEvict.keyGenerator());
        builder.setCacheManager(cacheEvict.cacheManager());
        builder.setCacheResolver(cacheEvict.cacheResolver());
        builder.setCacheWide(cacheEvict.allEntries());
        builder.setBeforeInvocation(cacheEvict.beforeInvocation());

        defaultConfig.applyDefault(builder);
        CacheEvictOperation op = builder.build();
        validateCacheOperation(ae, op);

        return op;
    }

    private CacheOperation parsePutAnnotation(
            AnnotatedElement ae, DefaultCacheConfig defaultConfig, ReactiveCachePut cachePut) {

        CachePutOperation.Builder builder = new CachePutOperation.Builder();

        builder.setName(ae.toString());
        builder.setCacheNames(cachePut.cacheNames());
        builder.setCondition(cachePut.condition());
        builder.setUnless(cachePut.unless());
        builder.setKey(cachePut.key());
        builder.setKeyGenerator(cachePut.keyGenerator());
        builder.setCacheManager(cachePut.cacheManager());
        builder.setCacheResolver(cachePut.cacheResolver());

        defaultConfig.applyDefault(builder);
        CachePutOperation op = builder.build();
        validateCacheOperation(ae, op);

        return op;
    }

    private void parseCachingAnnotation(
            AnnotatedElement ae, DefaultCacheConfig defaultConfig, ReactiveCaching caching, Collection<CacheOperation> ops) {

        ReactiveCacheable[] cacheables = caching.cacheable();
        for (ReactiveCacheable cacheable : cacheables) {
            ops.add(parseCacheableAnnotation(ae, defaultConfig, cacheable));
        }
        ReactiveCacheEvict[] cacheEvicts = caching.evict();
        for (ReactiveCacheEvict cacheEvict : cacheEvicts) {
            ops.add(parseEvictAnnotation(ae, defaultConfig, cacheEvict));
        }
        ReactiveCachePut[] cachePuts = caching.put();
        for (ReactiveCachePut cachePut : cachePuts) {
            ops.add(parsePutAnnotation(ae, defaultConfig, cachePut));
        }
    }


    /**
     * Validates the specified {@link CacheOperation}.
     * <p>Throws an {@link IllegalStateException} if the state of the operation is
     * invalid. As there might be multiple sources for default values, this ensure
     * that the operation is in a proper state before being returned.
     * @param ae the annotated element of the cache operation
     * @param operation the {@link CacheOperation} to validate
     */
    private void validateCacheOperation(AnnotatedElement ae, CacheOperation operation) {
        if (StringUtils.hasText(operation.getKey()) && StringUtils.hasText(operation.getKeyGenerator())) {
            throw new IllegalStateException("Invalid cache annotation configuration on '" +
                    ae.toString() + "'. Both 'key' and 'keyGenerator' attributes have been set. " +
                    "These attributes are mutually exclusive: either set the SpEL expression used to" +
                    "compute the key at runtime or set the name of the KeyGenerator bean to use.");
        }
        if (StringUtils.hasText(operation.getCacheManager()) && StringUtils.hasText(operation.getCacheResolver())) {
            throw new IllegalStateException("Invalid cache annotation configuration on '" +
                    ae.toString() + "'. Both 'cacheManager' and 'cacheResolver' attributes have been set. " +
                    "These attributes are mutually exclusive: the cache manager is used to configure a" +
                    "default cache resolver if none is set. If a cache resolver is set, the cache manager" +
                    "won't be used.");
        }
    }

    @Override
    public boolean equals(Object other) {
        return (this == other || other instanceof SpringReactiveCacheAnnotationParser);
    }

    @Override
    public int hashCode() {
        return SpringReactiveCacheAnnotationParser.class.hashCode();
    }


    /**
     * Provides default settings for a given set of cache operations.
     */
    private static class DefaultCacheConfig {

        private final Class<?> target;

        @Nullable
        private String[] cacheNames;

        @Nullable
        private String keyGenerator;

        @Nullable
        private String cacheManager;

        @Nullable
        private String cacheResolver;

        private boolean initialized = false;

        public DefaultCacheConfig(Class<?> target) {
            this.target = target;
        }

        /**
         * Apply the defaults to the specified {@link CacheOperation.Builder}.
         * @param builder the operation builder to update
         */
        public void applyDefault(CacheOperation.Builder builder) {
            if (!this.initialized) {
                ReactiveCacheConfig annotation = AnnotatedElementUtils.findMergedAnnotation(this.target, ReactiveCacheConfig.class);
                if (annotation != null) {
                    this.cacheNames = annotation.cacheNames();
                    this.keyGenerator = annotation.keyGenerator();
                    this.cacheManager = annotation.cacheManager();
                    this.cacheResolver = annotation.cacheResolver();
                }
                this.initialized = true;
            }

            if (builder.getCacheNames().isEmpty() && this.cacheNames != null) {
                builder.setCacheNames(this.cacheNames);
            }
            if (!StringUtils.hasText(builder.getKey()) && !StringUtils.hasText(builder.getKeyGenerator()) &&
                    StringUtils.hasText(this.keyGenerator)) {
                builder.setKeyGenerator(this.keyGenerator);
            }

            if (StringUtils.hasText(builder.getCacheManager()) || StringUtils.hasText(builder.getCacheResolver())) {
                // One of these is set so we should not inherit anything
            }
            else if (StringUtils.hasText(this.cacheResolver)) {
                builder.setCacheResolver(this.cacheResolver);
            }
            else if (StringUtils.hasText(this.cacheManager)) {
                builder.setCacheManager(this.cacheManager);
            }
        }
    }
}
