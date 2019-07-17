package com.synet.cache.annotation;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synet.cache.ReactiveCacheManager;
import com.synet.cache.redis.DefaultReactiveRedisCacheWriter;
import com.synet.cache.redis.ReactiveRedisCacheManager;
import com.synet.cache.redis.ReactiveRedisCacheWriter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@SpringBootApplication
@EnableReactiveCaching
public class AnnotationTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnotationTestApplication.class, args);
    }

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        //connection factory
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration("192.168.99.108");
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public ReactiveCacheManager createReactiveCacheManager(LettuceConnectionFactory lettuceConnectionFactory) {

        //cache writer
        ReactiveRedisCacheWriter cacheWriter = new DefaultReactiveRedisCacheWriter(lettuceConnectionFactory);

        //cache config
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(objectMapper);
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        cacheConfiguration = cacheConfiguration.serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(serializer));

        //cache manager
        ReactiveRedisCacheManager.RedisCacheManagerBuilder builder = ReactiveRedisCacheManager.builder(cacheWriter);
        ReactiveRedisCacheManager redisCacheManager = builder
                .cacheDefaults(cacheConfiguration)
                .build();


        return redisCacheManager;
    }

}
