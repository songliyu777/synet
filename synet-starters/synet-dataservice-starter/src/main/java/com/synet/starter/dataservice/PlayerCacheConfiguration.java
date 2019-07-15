package com.synet.starter.dataservice;

import com.synet.net.data.context.StateEntity;
import com.synet.net.data.manager.PlayerEntityConfig;
import com.synet.net.data.manager.PlayerEntityIdentifierFactory;
import com.synet.net.data.manager.PlayerEntityManager;
import com.synet.net.data.redis.PlayerCacheKeyFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import org.springside.modules.utils.mapper.JsonMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableConfigurationProperties(value = PlayerCacheProperties.class)
public class PlayerCacheConfiguration {

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveJsonObjectRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder = RedisSerializationContext
                .newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, Object> serializationContext = builder
                .value(new GenericJackson2JsonRedisSerializer("_type")).build();

        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }

    @Bean
    public PlayerEntityManager playerEntityManager(PlayerCacheProperties playerCacheProperties,
                                                   ReactiveRedisTemplate<String, Object> reactiveRedisTemplate,
                                                   ReactiveMongoTemplate reactiveMongoTemplate) {
        log.info("player cache properties is : " + JsonMapper.INSTANCE.toJson(playerCacheProperties));
        Assert.notNull(playerCacheProperties.getTtl(), "必须配置缓存过期时间");
        Map<Class<? extends StateEntity>, String> cacheKeys = new HashMap<>();
        Map<Class<? extends StateEntity>, String> playerKeys = new HashMap<>();
        List<PlayerEntityConfig> entities = playerCacheProperties.getEntities();
        entities.forEach(item -> {
            Class<? extends StateEntity> entityClass = item.getEntityClass();
            //mongo
            String playerKey = item.getPlayerKey();
            if (StringUtils.isNoneBlank(playerKey)) {
                playerKeys.put(entityClass, playerKey);
            }

            //缓存
            String cacheKey = item.getCacheKey();
            if (StringUtils.isNoneBlank(cacheKey)) {
                cacheKeys.put(entityClass, cacheKey);
            }
        });

        PlayerCacheKeyFactory factory = new PlayerCacheKeyFactory(cacheKeys, playerCacheProperties.getTtl());
        PlayerEntityIdentifierFactory identifierFactory = new PlayerEntityIdentifierFactory(playerKeys);
        return new PlayerEntityManager(factory, identifierFactory, reactiveMongoTemplate, reactiveRedisTemplate);
    }
}
