package com.synet.cache.autoconfigure;

import org.springframework.boot.autoconfigure.cache.CacheType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "synet.cache")
public class ReactiveCacheProperties {

    /**
     * Cache type. By default, auto-detected according to the environment.
     */
    private CacheType type;

    /**
     * Comma-separated list of cache names to create if supported by the underlying cache
     * manager. Usually, this disables the ability to create additional caches on-the-fly.
     */
    private List<String> cacheNames = new ArrayList<>();

    private final ReactiveCacheProperties.Redis redis = new ReactiveCacheProperties.Redis();

    public CacheType getType() {
        return this.type;
    }

    public void setType(CacheType mode) {
        this.type = mode;
    }

    public List<String> getCacheNames() {
        return this.cacheNames;
    }

    public void setCacheNames(List<String> cacheNames) {
        this.cacheNames = cacheNames;
    }

    public ReactiveCacheProperties.Redis getRedis() {
        return this.redis;
    }

    /**
     * Resolve the config location if set.
     * @param config the config resource
     * @return the location or {@code null} if it is not set
     * @throws IllegalArgumentException if the config attribute is set to an unknown
     * location
     */
    public Resource resolveConfigLocation(Resource config) {
        if (config != null) {
            Assert.isTrue(config.exists(), () -> "Cache configuration does not exist '"
                    + config.getDescription() + "'");
            return config;
        }
        return null;
    }


    /**
     * Redis-specific cache properties.
     */
    public static class Redis {

        /**
         * Entry expiration. By default the entries never expire.
         */
        private Duration timeToLive;

        /**
         * Allow caching null values.
         */
        private boolean cacheNullValues = true;

        /**
         * Key prefix.
         */
        private String keyPrefix;

        /**
         * Whether to use the key prefix when writing to Redis.
         */
        private boolean useKeyPrefix = true;

        public Duration getTimeToLive() {
            return this.timeToLive;
        }

        public void setTimeToLive(Duration timeToLive) {
            this.timeToLive = timeToLive;
        }

        public boolean isCacheNullValues() {
            return this.cacheNullValues;
        }

        public void setCacheNullValues(boolean cacheNullValues) {
            this.cacheNullValues = cacheNullValues;
        }

        public String getKeyPrefix() {
            return this.keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public boolean isUseKeyPrefix() {
            return this.useKeyPrefix;
        }

        public void setUseKeyPrefix(boolean useKeyPrefix) {
            this.useKeyPrefix = useKeyPrefix;
        }

    }
}
