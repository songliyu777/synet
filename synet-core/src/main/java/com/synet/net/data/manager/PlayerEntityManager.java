package com.synet.net.data.manager;

import com.synet.net.data.context.EntityStateContext;
import com.synet.net.data.context.StateEntity;
import com.synet.net.data.context.StateEntityHolder;
import com.synet.net.data.redis.PlayerCacheKeyFactory;
import com.synet.net.data.redis.RedisLock;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springside.modules.utils.collection.CollectionUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerEntityManager {

    private final static String PLAYER_LOCK_PREFIX = "p:lock::";

    private PlayerEntityTransactionDao playerEntityTransactionDao = new PlayerEntityTransactionDao();

    private PlayerEntityRedisDao playerEntityRedisDao;

    private PlayerCacheKeyFactory cacheKeyFactory;

    private PlayerEntityIdentifierFactory identifierFactory;

    private ReactiveMongoTemplate reactiveMongoTemplate;

    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    private RedisLock redisLock;

    public PlayerEntityManager(PlayerCacheKeyFactory cacheKeyFactory,
                               PlayerEntityIdentifierFactory identifierFactory,
                               ReactiveMongoTemplate reactiveMongoTemplate,
                               ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.cacheKeyFactory = cacheKeyFactory;
        this.identifierFactory = identifierFactory;
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.playerEntityRedisDao = new PlayerEntityRedisDao(reactiveRedisTemplate, cacheKeyFactory);
        this.redisLock = new RedisLock(reactiveRedisTemplate);
    }

    /**
     * 获取锁令牌
     *
     * @param scene 场景值
     * @return 返回锁token
     */
    public String getLockToken(String scene) {
        return redisLock.getLockToken(scene);
    }

    /**
     * 加锁
     *
     * @param playerId 玩家id
     * @param token 令牌
     * @param seconds 超时
     * @return 加锁状态
     */
    public Mono<Boolean> lock(Long playerId, String token, Long seconds) {
        return redisLock.lock(PLAYER_LOCK_PREFIX + String.valueOf(playerId), token, seconds);
    }

    /**
     * 加锁
     *
     * @param playerId 玩家id
     * @param token 令牌
     * @param seconds 超时
     * @return 加锁状态
     */
    public Mono<Boolean> lock(Long playerId, String token, Long seconds, Long retry) {
        return redisLock.lock(PLAYER_LOCK_PREFIX + String.valueOf(playerId), token, seconds, retry);
    }

    /**
     * 解锁
     *
     * @param playerId 玩家id
     * @return 解锁状态
     */
    public Mono<Boolean> unlock(Long playerId, String token) {
        return redisLock.unlock(PLAYER_LOCK_PREFIX + String.valueOf(playerId), token);
    }

    /**
     * 修改实体的状态，修改/删除
     *
     * @param context 状态上下文
     * @return 状态上下文或错误
     */
    public Mono<EntityStateContext> change(EntityStateContext context) {
        if (CollectionUtil.isEmpty(context.getEntities())) {
            return Mono.error(new RuntimeException("EntityStateContext is empty"));
        }

        //删除redis
        Mono<EntityStateContext> deleteCachedMono = doDeleteRedisOperation(context);

        //存mongo
        Mono<EntityStateContext> mongoMono = doMongoTransactionOperation(context);

        //存redis
        Mono<EntityStateContext> saveCachedMono = doCacheRedisOperation(context);
        return deleteCachedMono.then(mongoMono).then(saveCachedMono);
    }

    private Mono<EntityStateContext> doDeleteRedisOperation(EntityStateContext context) {
        List<StateEntityHolder> entities = context.getEntities();
        List<StateEntityHolder> cacheEntityHolders = entities.stream().filter(item -> cacheKeyFactory.needCache(item.getEntity())).collect(Collectors.toList());
        Map<? extends Class<? extends StateEntity>, List<StateEntityHolder>> cacheClassMap = cacheEntityHolders.stream().collect(Collectors.groupingBy(item -> item.getEntity().getClass()));
        return reactiveRedisTemplate.execute(action -> {
            return Flux.fromIterable(cacheClassMap.entrySet())
                    .flatMap(entry -> {
                        Class<? extends StateEntity> clazz = entry.getKey();
                        List<StateEntityHolder> entityHolders = entry.getValue();

                        String cacheKey = cacheKeyFactory.getCacheKey(clazz, context.getPlayerId());
                        List<String> hashKeys = entityHolders.stream().map(item -> cacheKeyFactory.getHashKey(item.getEntity().getClass(), context.getPlayerId(), item.getEntity().getId())).collect(Collectors.toList());

                        ByteBuffer cacheKeyByteBuffer = reactiveRedisTemplate.getSerializationContext().getStringSerializationPair().write(cacheKey);
                        List<ByteBuffer> hashKeyByteBuffers = hashKeys.stream().map(item -> reactiveRedisTemplate.getSerializationContext().getHashKeySerializationPair().write(item)).collect(Collectors.toList());
                        return action.hashCommands().hDel(cacheKeyByteBuffer, hashKeyByteBuffers);
                    });
        }).then(Mono.just(context));
    }

    private Mono<EntityStateContext> doCacheRedisOperation(EntityStateContext context) {
        List<StateEntityHolder> entities = context.getEntities();
        List<StateEntityHolder> cacheEntityHolders = entities.stream().filter(item -> cacheKeyFactory.needCache(item.getEntity())).collect(Collectors.toList());
        List<StateEntityHolder> saveStateEntityHolders = cacheEntityHolders.stream().filter(item -> item.getState() == StateEntityHolder.STATE_SAVE).collect(Collectors.toList());
        Map<? extends Class<? extends StateEntity>, List<StateEntity>> saveStateClassMap = saveStateEntityHolders.stream().map(StateEntityHolder::getEntity).collect(Collectors.groupingBy(item -> item.getClass()));
        return reactiveRedisTemplate.execute(action -> {
            return Flux.fromIterable(saveStateClassMap.entrySet())
                    .flatMap(entry -> {
                        Class<? extends StateEntity> clazz = entry.getKey();
                        List<? extends StateEntity> entityList = entry.getValue();
                        return playerEntityRedisDao.doEntityRedisCache(clazz, entityList, context.getPlayerId(), action);
                    });
        }).then(Mono.just(context));
    }

    private Mono<EntityStateContext> doMongoTransactionOperation(EntityStateContext context) {
        Flux<EntityStateContext> transResult = reactiveMongoTemplate.inTransaction().execute(action -> {
            Mono<StateEntity> chain = Mono.empty();
            for (StateEntityHolder item : context.getEntities()) {
                Mono<StateEntity> opMono = Mono.just(item).flatMap(holder -> {
                    if (holder.getState() == StateEntityHolder.STATE_SAVE) {
                        return playerEntityTransactionDao.save(action, holder.getEntity());
                    } else if (holder.getState() == StateEntityHolder.STATE_DELETE) {
                        return playerEntityTransactionDao.delete(action, holder.getEntity());
                    }
                    return Mono.error(new RuntimeException("not supported entity state"));
                });
                chain = chain.then(opMono);
            }
            return chain.then(Mono.just(context));
        });
        return transResult.collectList().flatMap(contexts -> Mono.just(contexts.get(0)));
    }

    /**
     * 查找玩家的数据，返回列表
     *
     * @param playerId 玩家id
     * @param clazz 类
     * @param <T> 实例
     * @return 实例
     */
    public <T extends StateEntity> Flux<T> findByPlayerId(Long playerId, Class<T> clazz) {
       return findByPlayerId(identifierFactory.getIdentifier(clazz), playerId, clazz);
    }

    /**
     * 查找玩家的数据，返回列表
     *
     * @param playerId 玩家id
     * @param clazz 类
     * @param <T> 实例
     * @return 实例
     */
    public <T extends StateEntity> Flux<T> findByPlayerId(String playerKey, Long playerId, Class<T> clazz) {
        //cache
        Boolean hasCache = cacheKeyFactory.needCache(clazz);
        Flux<T> cacheFlux = Flux.empty();
        if (hasCache) {
            cacheFlux = playerEntityRedisDao.findAll(playerId, clazz);
        }

        //mongo
        Flux<T> mongoFlux = playerEntityTransactionDao.findAllByPlayerId(reactiveMongoTemplate, playerKey, playerId, clazz);

        //cache write
        Flux<T> mongoCacheFlux = mongoFlux.collectList().flatMapMany(entities -> {
            if (hasCache) {
                return playerEntityRedisDao.doEntityRedisCache(clazz, entities, playerId);
            }
            return Flux.fromIterable(entities);
        });

        return cacheFlux.switchIfEmpty(mongoCacheFlux);
    }

    /**
     * 查找玩家的数据，返回列表
     *
     * @param playerId 玩家id
     * @param id 玩家id
     * @param clazz 类
     * @param <T> 实例
     * @return 实例
     */
    public <T extends StateEntity> Mono<T> findOneByPlayerIdAndId(Long playerId, Long id, Class<T> clazz) {
        //cache
        Boolean hasCache = cacheKeyFactory.needCache(clazz);
        Mono<T> cacheMono = Mono.empty();
        if (hasCache) {
            cacheMono = playerEntityRedisDao.findOne(playerId, id, clazz);
        }

        //mongo
        Mono<T> mongoMono = playerEntityTransactionDao.findOneById(reactiveMongoTemplate, id, clazz);

        //cache write
        Mono<T> mongoCacheMono = mongoMono.flatMap(item -> {
            if (hasCache) {
                return playerEntityRedisDao.doEntityRedisCache(item, playerId);
            }
            return Mono.just(item);
        });
        return cacheMono.switchIfEmpty(mongoCacheMono);
    }
}
