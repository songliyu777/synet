package com.synet.net.data.redis;

import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springside.modules.utils.misc.IdGenerator;
import reactor.core.publisher.Mono;
import reactor.retry.Retry;

import java.nio.ByteBuffer;

/**
 * Redis 分布式锁
 */
public class RedisLock {

    private final String LUA_SCRIPT = "if redis.call(\"get\",KEYS[1]) == ARGV[1]\n" +
            "then\n" +
            "    return redis.call(\"del\",KEYS[1])\n" +
            "else\n" +
            "    return 0\n" +
            "end";

    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    public RedisLock(ReactiveRedisTemplate<String, Object> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    /**
     * 获取锁令牌
     *
     * @param scene 场景值
     * @return 返回锁token
     */
    public String getLockToken(String scene) {
        return scene + ":" + IdGenerator.uuid();
    }

    /**
     * 加锁
     *
     * @param key 锁名字
     * @param token 令牌
     * @param seconds 超时
     * @return 加锁状态
     */
    public Mono<Boolean> lock(String key, String token, Long seconds) {
        return reactiveRedisTemplate.execute(action -> {
            ByteBuffer keyByteBuffer = reactiveRedisTemplate.getSerializationContext().getKeySerializationPair().write(key);
            ByteBuffer valueByteBuffer = reactiveRedisTemplate.getSerializationContext().getStringSerializationPair().write(token);
            return action.stringCommands().set(keyByteBuffer, valueByteBuffer, Expiration.seconds(seconds), RedisStringCommands.SetOption.SET_IF_ABSENT);
        }).collectList().map(list -> list.get(0));
    }

    /**
     * 加锁
     *
     * @param key 锁名字
     * @param token 令牌
     * @param seconds 超时
     * @return 加锁状态
     */
    public Mono<Boolean> lock(String key, String token, Long seconds, Long retry) {
        return lock(key, token, seconds)
                .map(locked -> {
                    if (!locked) {
                        throw new RedisLockException(key);
                    }
                    return locked;
                })
                .retryWhen(Retry.anyOf(RuntimeException.class).retryMax(retry));
    }

    /**
     * 解锁
     *
     * @param key 锁名字
     * @return 解锁状态
     */
    public Mono<Boolean> unlock(String key, String token) {
        return reactiveRedisTemplate.execute(action -> {
            ByteBuffer keyByteBuffer = reactiveRedisTemplate.getSerializationContext().getKeySerializationPair().write(key);
            ByteBuffer tokenByteBuffer = reactiveRedisTemplate.getSerializationContext().getStringSerializationPair().write(token);
            ByteBuffer luaScriptByteBuffer = reactiveRedisTemplate.getSerializationContext().getStringSerializationPair().write(LUA_SCRIPT);
            return action.scriptingCommands().eval(luaScriptByteBuffer, ReturnType.BOOLEAN, 1, keyByteBuffer, tokenByteBuffer).single().cast(Boolean.class);
        }).collectList().map(list -> list.get(0));
    }

    public static class RedisLockException extends RuntimeException {

        public RedisLockException(String key) {
            super("Lock key ["+ key+"] failed");
        }
    }
}
