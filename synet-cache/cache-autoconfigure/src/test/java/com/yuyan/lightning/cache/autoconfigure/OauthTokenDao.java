package com.yuyan.lightning.cache.autoconfigure;

import com.yuyan.lightning.cache.annotation.ReactiveCacheEvict;
import com.yuyan.lightning.cache.annotation.ReactiveCachePut;
import com.yuyan.lightning.cache.annotation.ReactiveCacheable;
import com.yuyan.lightning.cache.annotation.ReactiveCaching;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class OauthTokenDao {

    private List<OauthTokenEntity> entities;

    public OauthTokenDao() {
        this.entities = new ArrayList<>();
        for (int i = 10000 ; i <= 10005; ++i) {
            OauthTokenEntity entity = new OauthTokenEntity();
            entity.setId(Long.valueOf(i));
            entity.setAccessToken("accesstoken" + i);
            entity.setRefreshToken("refreshtoken" + i);
            this.entities.add(entity);
        }
    }

    /**
     * 保存
     *
     * @param entity 实体
     * @return 实体
     */
    @ReactiveCaching(put = {
            @ReactiveCachePut(value = "oauth:token:id", key = "#result.id"),
            @ReactiveCachePut(value = "oauth:token:access_token", key = "#result.accessToken"),
            @ReactiveCachePut(value = "oauth:token:refresh_token", key = "#result.refreshToken")
    })
    public Mono<OauthTokenEntity> save(OauthTokenEntity entity){
        return Mono.just(entity);
    }

    /**
     * 根据刷新令牌查找
     *
     * @param refreshToken 刷新令牌
     * @return 令牌对象
     */
    @ReactiveCaching(cacheable = {
            @ReactiveCacheable(value = "oauth:token:refresh_token", key = "#refreshToken", unless = "#result == null")
    })
    public Mono<OauthTokenEntity> findByRefreshToken(String refreshToken){
        Map<String, OauthTokenEntity> entityMap = this.entities.stream().collect(Collectors.toMap(OauthTokenEntity::getRefreshToken, item -> item));
        OauthTokenEntity entity = entityMap.get(refreshToken);
        return Mono.just(entity);
    }

    @ReactiveCaching(cacheable = {
            @ReactiveCacheable(value = "oauth:token:refresh_token", key = "#key", unless = "#result == null")
    })
    public Flux<OauthTokenEntity> findByAllTokens(String key){
        return Flux.fromIterable(entities);
    }

    /**
     * 根据访问令牌查找
     *
     * @param accessToken 访问令牌
     * @return 令牌对象
     */
    @ReactiveCaching(cacheable = {
            @ReactiveCacheable(value = "oauth:token:access_token", key = "#accessToken", unless = "#result == null"),
    })
    public Mono<OauthTokenEntity> findByAccessToken(String accessToken){
        Map<String, OauthTokenEntity> entityMap = this.entities.stream().collect(Collectors.toMap(OauthTokenEntity::getAccessToken, item -> item));
        OauthTokenEntity entity = entityMap.get(accessToken);
        return Mono.just(entity);
    }

    @ReactiveCaching(evict = {
            @ReactiveCacheEvict(value = "oauth:token:id", key = "#result.id"),
            @ReactiveCacheEvict(value = "oauth:token:access_token", key = "#result.accessToken"),
            @ReactiveCacheEvict(value = "oauth:token:refresh_token", key = "#result.refreshToken")
    })
    public Mono<OauthTokenEntity> deleteById(Long id) {
        Map<Long, OauthTokenEntity> entityMap = this.entities.stream().collect(Collectors.toMap(OauthTokenEntity::getId, item -> item));
        OauthTokenEntity entity = entityMap.get(id);
        if (Objects.nonNull(entity)) {
            this.entities.remove(entity);
        }
        return Mono.just(entity);
    }
}
