package com.synet.net.data.redis;

import com.synet.net.data.context.StateEntity;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 玩家缓存key工程
 */
public class PlayerCacheKeyFactory {

    public final static String PLAYER_PREFIX = "p:";

    private Map<Class<? extends StateEntity>, String> cacheKeys;

    private Long cacheTtl;

    public PlayerCacheKeyFactory(Map<Class<? extends StateEntity>, String> cacheKeys, Long cacheTtl) {
        this.cacheKeys = cacheKeys;
        this.cacheTtl = cacheTtl;
    }


    private String getCacheKey(Class<? extends StateEntity> clazz) {
        return cacheKeys.get(clazz);
    }

    /**
     * 获取缓存key
     *
     * @param clazz
     * @param playerId
     * @return
     */
    public String getCacheKey(Class<? extends StateEntity> clazz, Long playerId) {
        String cacheKey = getCacheKey(clazz);
        Assert.notNull(cacheKey, "cache key is null");
        Assert.notNull(playerId, "playerId is null");
        return PLAYER_PREFIX + playerId + ":" + cacheKey;
    }

//    /**
//     * 获取缓存key
//     *
//     * @param entity 实体
//     * @return 缓存key
//     */
//    public String getCacheKey(StateEntity entity) {
//        return getCacheKey(entity.getClass(), entity.getPlayerId());
//    }

    /**
     * 获取hashKey
     * @param clazz 实体
     * @return hash key
     */
    public String getHashKey(Class<? extends StateEntity> clazz, Long playerId, Long hashId) {
        String cacheKey = getCacheKey(clazz, playerId);
        Assert.notNull(hashId, "id is null");
        return cacheKey + "::" + hashId;
    }

//    /**
//     * 获取hashKey
//     * @param entity 实体
//     * @return hash key
//     */
//    public String getHashKey(StateEntity entity) {
//        return getHashKey(entity.getClass(), entity.getPlayerId(), entity.getId());
//    }

    /**
     * 是否需要缓存
     *
     * @param clazz 缓存的类
     * @return 是/否
     */
    public Boolean needCache(Class<? extends StateEntity> clazz) {
        return cacheKeys.containsKey(clazz);
    }

    /**
     * 是否需要缓存
     *
     * @param entity 缓存的实例
     * @return 是/否
     */
    public Boolean needCache(StateEntity entity) {
        return needCache(entity.getClass());
    }

    /**
     * 获取缓存超时时间
     *
     * @return
     */
    public Long getCacheTtl() {
        return cacheTtl;
    }
}
