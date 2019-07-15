package com.synet.net.data.manager;

import com.google.common.collect.Maps;
import com.synet.net.data.context.StateEntity;
import com.synet.net.data.redis.PlayerCacheKeyFactory;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springside.modules.utils.mapper.JsonMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.List;
import java.util.Map;

public class PlayerEntityRedisDao {

    private ReactiveRedisTemplate reactiveRedisTemplate;

    private PlayerCacheKeyFactory cacheKeyFactory;

    public PlayerEntityRedisDao(ReactiveRedisTemplate reactiveRedisTemplate, PlayerCacheKeyFactory cacheKeyFactory) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.cacheKeyFactory = cacheKeyFactory;
    }

    /**
     * 查找一个
     *
     * @param playerId
     * @param id
     * @param clazz
     * @param <T>
     * @return
     */
    public  <T extends StateEntity> Mono<T> findOne(Long playerId, Long id, Class<T> clazz) {
        Mono<T> cacheMono;
        String cacheKey = cacheKeyFactory.getCacheKey(clazz, playerId);
        String hashKey = cacheKeyFactory.getHashKey(clazz, playerId, id);
        ReactiveHashOperations<String, String, Object> hashOperations = reactiveRedisTemplate.opsForHash();
        cacheMono = hashOperations.get(cacheKey, hashKey).map(item -> JsonMapper.INSTANCE.fromJson((String) item, clazz));
        return cacheMono;
    }

    /**
     * 查找所有
     *
     * @param playerId
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends StateEntity> Flux<T> findAll(Long playerId, Class<T> clazz) {
        Flux<T> cacheFlux;
        String cacheKey = cacheKeyFactory.getCacheKey(clazz, playerId);
        ReactiveHashOperations<String, String, Object> hashOperations = reactiveRedisTemplate.opsForHash();
        cacheFlux = hashOperations.entries(cacheKey).map(entry -> JsonMapper.INSTANCE.fromJson((String) entry.getValue(), clazz));
        return cacheFlux;
    }

    /**
     * 实体redis缓存
     *
     * @param clazz
     * @param entities
     * @param playerId
     * @param <T>
     * @return
     */
    public <T extends StateEntity> Flux<? extends T> doEntityRedisCache(Class<T> clazz, List<T> entities, Long playerId) {
        return reactiveRedisTemplate.execute(action -> doEntityRedisCache(clazz, entities, playerId, action)).flatMapIterable(item -> entities);
    }

    public Mono<Boolean> doEntityRedisCache(Class<? extends StateEntity> clazz, List<? extends StateEntity> entities, Long playerId, ReactiveRedisConnection action) {
        String cacheKey = cacheKeyFactory.getCacheKey(clazz, playerId);
        ByteBuffer cacheKeyByteBuffer = reactiveRedisTemplate.getSerializationContext().getStringSerializationPair().write(cacheKey);

        Map<ByteBuffer, ByteBuffer> hashCacheMaps = Maps.newHashMap();
        entities.forEach(item -> {
            String hashKey = cacheKeyFactory.getHashKey(item.getClass(), playerId, item.getId());
            ByteBuffer hashKeyByteBuffer = reactiveRedisTemplate.getSerializationContext().getHashKeySerializationPair().write(hashKey);
            ByteBuffer valueByteBuffer = reactiveRedisTemplate.getSerializationContext().getValueSerializationPair().write(item);
            hashCacheMaps.put(hashKeyByteBuffer, valueByteBuffer);
        });

        return action.hashCommands().hMSet(cacheKeyByteBuffer, hashCacheMaps).flatMap(result -> {
            if (result) {
                return action.keyCommands().expire(cacheKeyByteBuffer, Duration.ofSeconds(cacheKeyFactory.getCacheTtl()));
            }
            return Mono.just(result);
        });
    }

    /**
     * 实体redis缓存
     *
     * @param item
     * @param playerId
     * @param <T>
     * @return
     */
    public <T extends StateEntity> Mono<? extends T> doEntityRedisCache(T item, Long playerId) {
        return reactiveRedisTemplate.execute(action -> {
            String cacheKey = cacheKeyFactory.getCacheKey(item.getClass(), playerId);
            String hashKey = cacheKeyFactory.getHashKey(item.getClass(), playerId, item.getId());
            ByteBuffer cacheKeyByteBuffer = reactiveRedisTemplate.getSerializationContext().getStringSerializationPair().write(cacheKey);
            ByteBuffer hashKeyByteBuffer = reactiveRedisTemplate.getSerializationContext().getHashKeySerializationPair().write(hashKey);
            ByteBuffer valueByteBuffer = reactiveRedisTemplate.getSerializationContext().getValueSerializationPair().write(item);
            return action.hashCommands().hSet(cacheKeyByteBuffer, hashKeyByteBuffer, valueByteBuffer).flatMap(result -> {
                if (result) {
                    return action.keyCommands().expire(cacheKeyByteBuffer, Duration.ofSeconds(cacheKeyFactory.getCacheTtl()));
                }
                return Mono.just(result);
            });
        }).then(Mono.just(item));
    }
}
