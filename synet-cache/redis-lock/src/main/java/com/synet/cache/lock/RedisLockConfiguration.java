package com.synet.cache.lock;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@ConditionalOnClass(ReactiveRedisConnectionFactory.class)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnBean({ReactiveRedisConnectionFactory.class})
public class RedisLockConfiguration {
    @Bean
    ReactiveStringRedisTemplate reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveStringRedisTemplate(factory, RedisSerializationContext.string());
    }

    @Bean
    RedisLockFactory redisLockFactory(ReactiveStringRedisTemplate template) {
        return new RedisLockFactory(template);
    }
}
