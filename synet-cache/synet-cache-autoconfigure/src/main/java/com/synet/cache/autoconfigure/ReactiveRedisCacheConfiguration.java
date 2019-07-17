package com.synet.cache.autoconfigure;

import com.synet.cache.ReactiveCacheManager;
import com.synet.cache.redis.ReactiveRedisCacheManager;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.util.LinkedHashSet;
import java.util.List;

@Configuration
@ConditionalOnClass(ReactiveRedisConnectionFactory.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnBean({ReactiveRedisConnectionFactory.class})
@ConditionalOnMissingBean(ReactiveCacheManager.class)
@EnableConfigurationProperties(value = ReactiveCacheProperties.class)
public class ReactiveRedisCacheConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ReactiveCacheManagerCustomizers cacheManagerCustomizers() {
        return new ReactiveCacheManagerCustomizers(null);
    }

    @Bean
    public ReactiveRedisCacheManager cacheManager(ReactiveCacheProperties cacheProperties,
                                                  ReactiveCacheManagerCustomizers cacheManagerCustomizers,
                                                  ObjectProvider<RedisCacheConfiguration> redisCacheConfiguration,
                                                  ReactiveRedisConnectionFactory redisConnectionFactory,
                                                  ResourceLoader resourceLoader) {
        ReactiveRedisCacheManager.RedisCacheManagerBuilder builder = ReactiveRedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(determineConfiguration(cacheProperties,
                        redisCacheConfiguration, resourceLoader.getClassLoader()));
        List<String> cacheNames = cacheProperties.getCacheNames();
        if (!cacheNames.isEmpty()) {
            builder.initialCacheNames(new LinkedHashSet<>(cacheNames));
        }
        return cacheManagerCustomizers.customize(builder.build());
    }

    private RedisCacheConfiguration determineConfiguration(
            ReactiveCacheProperties cacheProperties,
            ObjectProvider<RedisCacheConfiguration> redisCacheConfiguration,
            ClassLoader classLoader) {
        return redisCacheConfiguration
                .getIfAvailable(() -> createConfiguration(cacheProperties, classLoader));

    }

    private RedisCacheConfiguration createConfiguration(
            ReactiveCacheProperties cacheProperties, ClassLoader classLoader) {
        ReactiveCacheProperties.Redis redisProperties = cacheProperties.getRedis();
        RedisCacheConfiguration config = RedisCacheConfiguration
                .defaultCacheConfig();
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(new JdkSerializationRedisSerializer(classLoader)));
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixKeysWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }

}
