package com.synet.server.logic.login;

import com.google.common.collect.Maps;
import com.synet.net.data.context.EntityStateContext;
import com.synet.net.data.context.StateEntity;
import com.synet.net.data.manager.PlayerEntityIdentifierFactory;
import com.synet.net.data.manager.PlayerEntityManager;
import com.synet.net.data.redis.PlayerCacheKeyFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.retry.Retry;
import reactor.test.StepVerifier;

import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class PlayerEntityManagerTests {

    @Autowired
    private ReactiveMongoTemplate template;

    @Autowired
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    @Test
    public void testSave() {
        Map<Class<? extends StateEntity>, String> cacheKeys = Maps.newHashMap();
        cacheKeys.put(UserEntity.class, "user");
        PlayerCacheKeyFactory factory = new PlayerCacheKeyFactory(cacheKeys, 10L);
        PlayerEntityIdentifierFactory identifierFactory = new PlayerEntityIdentifierFactory(Maps.newHashMap());

        PlayerEntityManager manager = new PlayerEntityManager(factory, identifierFactory, template, redisTemplate);
        UserEntity entity = new UserEntity();
        entity.setId(20000L);
        entity.setName("kkkkkk");
        entity.setAge(12);
        UserEntity entity2 = new UserEntity();
        entity2.setId(30000L);
        entity2.setName("kkkkkk2222222");
        entity2.setAge(1222);
        EntityStateContext context = EntityStateContext.builder()
                .palyer(20000L)
                .save(entity)
                .save(entity2);
        Mono<EntityStateContext> change = manager.change(context);
        StepVerifier.create(change)
                .expectNext(context)
                .verifyComplete();
    }

    @Test
    public void testSaveAndDelete() {
        Map<Class<? extends StateEntity>, String> cacheKeys = Maps.newHashMap();
        cacheKeys.put(UserEntity.class, "user");
        PlayerCacheKeyFactory factory = new PlayerCacheKeyFactory(cacheKeys, 10L);
        PlayerEntityIdentifierFactory identifierFactory = new PlayerEntityIdentifierFactory(Maps.newHashMap());

        PlayerEntityManager manager = new PlayerEntityManager(factory, identifierFactory, template, redisTemplate);
        UserEntity entity = new UserEntity();
        entity.setId(10000L);
        entity.setName("kkkkkk");
        entity.setAge(12);

        UserEntity entity2= new UserEntity();
        entity2.setId(20000L);
        entity2.setName("kkkkkk2");
        entity2.setAge(12);
        EntityStateContext context = EntityStateContext.builder()
                .palyer(10000L)
                .save(entity)
                .delete(entity2);

        Mono<EntityStateContext> change = manager.change(context);
        StepVerifier.create(change)
                .expectNext(context)
                .verifyComplete();
    }

    @Test
    public void testFind() {
        Map<Class<? extends StateEntity>, String> cacheKeys = Maps.newHashMap();
        cacheKeys.put(UserEntity.class, "user");
        PlayerCacheKeyFactory factory = new PlayerCacheKeyFactory(cacheKeys, 10L);
        Map<Class<? extends StateEntity>, String> identifierMap = Maps.newHashMap();
        identifierMap.put(UserEntity.class, "id");
        PlayerEntityIdentifierFactory identifierFactory = new PlayerEntityIdentifierFactory(identifierMap);

        PlayerEntityManager manager = new PlayerEntityManager(factory, identifierFactory, template, redisTemplate);
        Flux<UserEntity> byPlayerId = manager.findByPlayerId(10000L, UserEntity.class);

        StepVerifier.create(byPlayerId)
                .consumeNextWith(System.out::println)
                .verifyComplete();
    }

    @Test
    public void testFindOne() {
        Map<Class<? extends StateEntity>, String> cacheKeys = Maps.newHashMap();
        cacheKeys.put(UserEntity.class, "user");
        PlayerCacheKeyFactory factory = new PlayerCacheKeyFactory(cacheKeys, 10L);
        PlayerEntityIdentifierFactory identifierFactory = new PlayerEntityIdentifierFactory(Maps.newHashMap());

        PlayerEntityManager manager = new PlayerEntityManager(factory, identifierFactory, template, redisTemplate);

        Mono<UserEntity> entityMono = manager.findOneByPlayerIdAndId(20000L, 10000L, UserEntity.class);
        StepVerifier.create(entityMono)
                .consumeNextWith(System.out::println)
                .verifyComplete();
    }

    @Test
    public void testLock() {
        String key = "p:lock::";
        Long playerId = 20000L;

        Map<Class<? extends StateEntity>, String> cacheKeys = Maps.newHashMap();
        cacheKeys.put(UserEntity.class, "user");
        PlayerCacheKeyFactory factory = new PlayerCacheKeyFactory(cacheKeys, 10L);
        PlayerEntityIdentifierFactory identifierFactory = new PlayerEntityIdentifierFactory(Maps.newHashMap());

        PlayerEntityManager manager = new PlayerEntityManager(factory, identifierFactory, template, redisTemplate);

        //先确保key不存在
        Mono<Boolean> delete = redisTemplate.opsForValue().delete(key + playerId);
        String token = manager.getLockToken("login");
        Mono<Boolean> lock = manager.lock(playerId, token, 10L);
        Mono<Boolean> then = delete.then(lock);

        StepVerifier.create(then)
                .consumeNextWith(System.out::println)
                .verifyComplete();
    }

    @Test
    public void testLockTwice() {
        String key = "p:lock::";
        Long playerId = 20000L;

        Map<Class<? extends StateEntity>, String> cacheKeys = Maps.newHashMap();
        cacheKeys.put(UserEntity.class, "user");
        PlayerCacheKeyFactory factory = new PlayerCacheKeyFactory(cacheKeys, 10L);
        PlayerEntityIdentifierFactory identifierFactory = new PlayerEntityIdentifierFactory(Maps.newHashMap());

        PlayerEntityManager manager = new PlayerEntityManager(factory, identifierFactory, template, redisTemplate);

        //先确保key不存在
        Mono<Boolean> delete = redisTemplate.opsForValue().delete(key + playerId);
        String token = manager.getLockToken("login");
        Mono<Boolean> lock = manager.lock(playerId, token, 10L);
        Mono<Boolean> then = delete.then(lock).then(manager.lock(playerId, "login2xxxx", 10L));

        StepVerifier.create(then)
                .consumeNextWith(System.out::println)
                .verifyComplete();
    }

    @Test
    public void testUnlockExistKey() {
        Long playerId = 20000L;

        Map<Class<? extends StateEntity>, String> cacheKeys = Maps.newHashMap();
        cacheKeys.put(UserEntity.class, "user");
        PlayerCacheKeyFactory factory = new PlayerCacheKeyFactory(cacheKeys, 10L);
        PlayerEntityIdentifierFactory identifierFactory = new PlayerEntityIdentifierFactory(Maps.newHashMap());

        PlayerEntityManager manager = new PlayerEntityManager(factory, identifierFactory, template, redisTemplate);
        String token = manager.getLockToken("login");
        Mono<Boolean> booleanMono = manager.lock(playerId, token, 50L)
                .doOnNext(System.out::println)
                .flatMap(item -> {
                    System.out.println(123);
                    return manager.unlock(playerId, token);
                })
                .doOnNext(System.out::println);

        StepVerifier.create(booleanMono)
                .consumeNextWith(System.out::println)
                .verifyComplete();
    }

    @Test
    public void testUnlockNotExistKey() {
        Long playerId = 20000L;

        Map<Class<? extends StateEntity>, String> cacheKeys = Maps.newHashMap();
        cacheKeys.put(UserEntity.class, "user");
        PlayerCacheKeyFactory factory = new PlayerCacheKeyFactory(cacheKeys, 10L);
        PlayerEntityIdentifierFactory identifierFactory = new PlayerEntityIdentifierFactory(Maps.newHashMap());

        PlayerEntityManager manager = new PlayerEntityManager(factory, identifierFactory, template, redisTemplate);
        String token = manager.getLockToken("login");
        Mono<Boolean> booleanMono = manager.lock(playerId, token, 50L)
                .doOnNext(System.out::println)
                .flatMap(item -> {
                    System.out.println(123);
                    return manager.unlock(playerId, token + "123");
                })
                .doOnNext(System.out::println);

        StepVerifier.create(booleanMono)
                .consumeNextWith(System.out::println)
                .verifyComplete();
    }

    @Test
    public void testMono() {
        Mono<String> retry = Mono.just("123")
                .map(item -> {
                    int i = 1 / 0;
                    return item;
                }).doOnError(System.out::println)
                .retryWhen(Retry.anyOf(RuntimeException.class).retryMax(5))
                ;
        StepVerifier.create(retry)
                .verifyError();
    }
}
