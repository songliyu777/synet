package com.yuyan.lightning.cache.annotation;

import com.yuyan.lightning.cache.ReactiveCacheManager;
import com.yuyan.lightning.cache.interceptor.CacheResolver;
import com.yuyan.lightning.cache.interceptor.KeyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.function.Supplier;

@Configuration
public abstract class AbstractCachingConfiguration implements ImportAware {

    @Nullable
    protected AnnotationAttributes enableCaching;

    @Nullable
    protected Supplier<ReactiveCacheManager> cacheManager;

    @Nullable
    protected Supplier<CacheResolver> cacheResolver;

    @Nullable
    protected Supplier<KeyGenerator> keyGenerator;

    @Nullable
    protected Supplier<CacheErrorHandler> errorHandler;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableCaching = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableReactiveCaching.class.getName(), false));
        if (this.enableCaching == null) {
            throw new IllegalArgumentException(
                    "@EnableReactiveCaching is not present on importing class " + importMetadata.getClassName());
        }
    }

    @Autowired(required = false)
    void setConfigurers(Collection<ReactiveCachingConfigurer> configurers) {
        if (CollectionUtils.isEmpty(configurers)) {
            return;
        }
        if (configurers.size() > 1) {
            throw new IllegalStateException(configurers.size() + " implementations of " +
                    "CachingConfigurer were found when only 1 was expected. " +
                    "Refactor the configuration such that CachingConfigurer is " +
                    "implemented only once or not at all.");
        }
        ReactiveCachingConfigurer configurer = configurers.iterator().next();
        useCachingConfigurer(configurer);
    }

    /**
     * Extract the configuration from the nominated {@link CachingConfigurer}.
     */
    protected void useCachingConfigurer(ReactiveCachingConfigurer config) {
        this.cacheManager = config::cacheManager;
        this.cacheResolver = config::cacheResolver;
        this.keyGenerator = config::keyGenerator;
        this.errorHandler = config::errorHandler;
    }
}
