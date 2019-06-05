package com.synet.cache.redis;

import com.synet.cache.support.AbstractReactiveCacheManager;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.*;

public class ReactiveRedisCacheManager extends AbstractReactiveCacheManager {

    private final ReactiveRedisCacheWriter cacheWriter;
    private final RedisCacheConfiguration defaultCacheConfig;
    private final Map<String, RedisCacheConfiguration> initialCacheConfiguration;
    private final boolean allowInFlightCacheCreation;

    private ReactiveRedisCacheManager(ReactiveRedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration,
                              boolean allowInFlightCacheCreation) {

        Assert.notNull(cacheWriter, "CacheWriter must not be null!");
        Assert.notNull(defaultCacheConfiguration, "DefaultCacheConfiguration must not be null!");

        this.cacheWriter = cacheWriter;
        this.defaultCacheConfig = defaultCacheConfiguration;
        this.initialCacheConfiguration = new LinkedHashMap<>();
        this.allowInFlightCacheCreation = allowInFlightCacheCreation;
    }


    public ReactiveRedisCacheManager(ReactiveRedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        this(cacheWriter, defaultCacheConfiguration, true);
    }

    public ReactiveRedisCacheManager(ReactiveRedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration,
                             String... initialCacheNames) {

        this(cacheWriter, defaultCacheConfiguration, true, initialCacheNames);
    }

    public ReactiveRedisCacheManager(ReactiveRedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration,
                             boolean allowInFlightCacheCreation, String... initialCacheNames) {

        this(cacheWriter, defaultCacheConfiguration, allowInFlightCacheCreation);

        for (String cacheName : initialCacheNames) {
            this.initialCacheConfiguration.put(cacheName, defaultCacheConfiguration);
        }
    }

    public ReactiveRedisCacheManager(ReactiveRedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration,
                             Map<String, RedisCacheConfiguration> initialCacheConfigurations) {

        this(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations, true);
    }

    public ReactiveRedisCacheManager(ReactiveRedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration,
                             Map<String, RedisCacheConfiguration> initialCacheConfigurations, boolean allowInFlightCacheCreation) {

        this(cacheWriter, defaultCacheConfiguration, allowInFlightCacheCreation);

        Assert.notNull(initialCacheConfigurations, "InitialCacheConfigurations must not be null!");

        this.initialCacheConfiguration.putAll(initialCacheConfigurations);
    }

    public static ReactiveRedisCacheManager create(ReactiveRedisConnectionFactory connectionFactory) {

        Assert.notNull(connectionFactory, "ConnectionFactory must not be null!");

        return new ReactiveRedisCacheManager(new DefaultReactiveRedisCacheWriter(connectionFactory),
                RedisCacheConfiguration.defaultCacheConfig());
    }

    public static ReactiveRedisCacheManager.RedisCacheManagerBuilder builder(ReactiveRedisConnectionFactory connectionFactory) {

        Assert.notNull(connectionFactory, "ConnectionFactory must not be null!");

        return ReactiveRedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(connectionFactory);
    }

    public static ReactiveRedisCacheManager.RedisCacheManagerBuilder builder(ReactiveRedisCacheWriter cacheWriter) {

        Assert.notNull(cacheWriter, "CacheWriter must not be null!");

        return ReactiveRedisCacheManager.RedisCacheManagerBuilder.fromCacheWriter(cacheWriter);
    }

    @Override
    protected Collection<ReactiveRedisCache> loadCaches() {

        List<ReactiveRedisCache> caches = new LinkedList<>();

        for (Map.Entry<String, RedisCacheConfiguration> entry : initialCacheConfiguration.entrySet()) {
            caches.add(createRedisCache(entry.getKey(), entry.getValue()));
        }

        return caches;
    }

    @Override
    protected ReactiveRedisCache getMissingCache(String name) {
        return allowInFlightCacheCreation ? createRedisCache(name, defaultCacheConfig) : null;
    }

    public Map<String, RedisCacheConfiguration> getCacheConfigurations() {

        Map<String, RedisCacheConfiguration> configurationMap = new HashMap<>(getCacheNames().size());

        getCacheNames().forEach(it -> {

            RedisCache cache = RedisCache.class.cast(lookupCache(it));
            configurationMap.put(it, cache != null ? cache.getCacheConfiguration() : null);
        });

        return Collections.unmodifiableMap(configurationMap);
    }

    protected ReactiveRedisCache createRedisCache(String name, @Nullable RedisCacheConfiguration cacheConfig) {
        return new ReactiveRedisCache(name, cacheWriter, cacheConfig != null ? cacheConfig : defaultCacheConfig);
    }

    public static class RedisCacheManagerBuilder {

        private final ReactiveRedisCacheWriter cacheWriter;
        private RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        private final Map<String, RedisCacheConfiguration> initialCaches = new LinkedHashMap<>();
        boolean allowInFlightCacheCreation = true;

        private RedisCacheManagerBuilder(ReactiveRedisCacheWriter cacheWriter) {
            this.cacheWriter = cacheWriter;
        }

        public static ReactiveRedisCacheManager.RedisCacheManagerBuilder fromConnectionFactory(ReactiveRedisConnectionFactory connectionFactory) {

            Assert.notNull(connectionFactory, "ConnectionFactory must not be null!");

            return builder(new DefaultReactiveRedisCacheWriter(connectionFactory));
        }

        public static ReactiveRedisCacheManager.RedisCacheManagerBuilder fromCacheWriter(ReactiveRedisCacheWriter cacheWriter) {

            Assert.notNull(cacheWriter, "CacheWriter must not be null!");

            return new ReactiveRedisCacheManager.RedisCacheManagerBuilder(cacheWriter);
        }

        public ReactiveRedisCacheManager.RedisCacheManagerBuilder cacheDefaults(RedisCacheConfiguration defaultCacheConfiguration) {

            Assert.notNull(defaultCacheConfiguration, "DefaultCacheConfiguration must not be null!");

            this.defaultCacheConfiguration = defaultCacheConfiguration;

            return this;
        }

        public ReactiveRedisCacheManager.RedisCacheManagerBuilder initialCacheNames(Set<String> cacheNames) {

            Assert.notNull(cacheNames, "CacheNames must not be null!");

            Map<String, RedisCacheConfiguration> cacheConfigMap = new LinkedHashMap<>(cacheNames.size());
            cacheNames.forEach(it -> cacheConfigMap.put(it, defaultCacheConfiguration));

            return withInitialCacheConfigurations(cacheConfigMap);
        }

        public ReactiveRedisCacheManager.RedisCacheManagerBuilder withInitialCacheConfigurations(
                Map<String, RedisCacheConfiguration> cacheConfigurations) {

            Assert.notNull(cacheConfigurations, "CacheConfigurations must not be null!");
            cacheConfigurations.forEach((cacheName, configuration) -> Assert.notNull(configuration,
                    String.format("RedisCacheConfiguration for cache %s must not be null!", cacheName)));

            this.initialCaches.putAll(cacheConfigurations);

            return this;
        }

        public ReactiveRedisCacheManager.RedisCacheManagerBuilder disableCreateOnMissingCache() {

            this.allowInFlightCacheCreation = false;
            return this;
        }

        public ReactiveRedisCacheManager build() {

            ReactiveRedisCacheManager cm = new ReactiveRedisCacheManager(cacheWriter, defaultCacheConfiguration, initialCaches,
                    allowInFlightCacheCreation);
            return cm;
        }
    }
}
