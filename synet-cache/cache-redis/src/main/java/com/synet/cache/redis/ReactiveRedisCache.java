package com.synet.cache.redis;

import com.synet.cache.ReactiveCache;
import org.springframework.cache.support.NullValue;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.util.ByteUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;

public class ReactiveRedisCache implements ReactiveCache {
    private static final byte[] BINARY_NULL_VALUE = RedisSerializer.java().serialize(NullValue.INSTANCE);
    private final String name;
    private final ReactiveRedisCacheWriter reactiveRedisCacheWriter;
    private final RedisCacheConfiguration cacheConfig;
    private final ConversionService conversionService;
    private final boolean allowNullValues = false;

    public ReactiveRedisCache(String name, ReactiveRedisCacheWriter reactiveRedisCacheWriter, RedisCacheConfiguration cacheConfig) {
        this.name = name;
        this.reactiveRedisCacheWriter = reactiveRedisCacheWriter;
        this.cacheConfig = cacheConfig;
        this.conversionService = cacheConfig.getConversionService();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ReactiveRedisCacheWriter getNativeCache() {
        return reactiveRedisCacheWriter;
    }

    @Override
    public <T> Mono<T> get(Object key, Class<T> type) {
        return getByteBuffer(key).map(byteBuffer -> (T) deserializeCacheValue(byteBuffer.array()));
    }

    @Override
    public <T> Mono<T> get(Object key, Mono<T> valueLoader) {
        Mono<T> objectMono = get(key);
        return objectMono.switchIfEmpty(valueLoader.flatMap(value -> put(key, value).map(Void -> value)));
    }

    private Mono<ByteBuffer> getByteBuffer(Object key) {
        return reactiveRedisCacheWriter.get(name, ByteBuffer.wrap(createAndConvertCacheKey(key)));
    }

    private <T> Mono<T> get(Object key) {
        return getByteBuffer(key).map(byteBuffer -> (T) deserializeCacheValue(byteBuffer.array()));
    }

    @Override
    public Mono<Boolean> put(Object key, Object value) {
        Object cacheValue = preProcessCacheValue(value);

        if (!isAllowNullValues() && cacheValue == null) {

            return Mono.error(new IllegalArgumentException(String.format(
                    "Cache '%s' does not allow 'null' values. Avoid storing null via '@Cacheable(unless=\"#result == null\")' or configure RedisCache to allow 'null' via RedisCacheConfiguration.",
                    name)));
        }

        return reactiveRedisCacheWriter.put(name, ByteBuffer.wrap(createAndConvertCacheKey(key)), ByteBuffer.wrap(serializeCacheValue(cacheValue)), cacheConfig.getTtl());
    }

    /**
     * Customization hook called before passing object to
     * {@link org.springframework.data.redis.serializer.RedisSerializer}.
     *
     * @param value can be {@literal null}.
     * @return preprocessed value. Can be {@literal null}.
     */
    @Nullable
    protected Object preProcessCacheValue(@Nullable Object value) {

        if (value != null) {
            return value;
        }

        return isAllowNullValues() ? NullValue.INSTANCE : null;
    }

    @Override
    public Mono<Boolean> evict(Object key) {
        return reactiveRedisCacheWriter.remove(name, ByteBuffer.wrap(createAndConvertCacheKey(key))).map(item -> Boolean.TRUE);
    }

    @Override
    public Mono<Boolean> clear() {
        byte[] pattern = conversionService.convert(createCacheKey("*"), byte[].class);
        return reactiveRedisCacheWriter.clean(name, ByteBuffer.wrap(pattern)).map(item -> Boolean.TRUE);
    }

    /**
     * Serialize the key.
     *
     * @param cacheKey must not be {@literal null}.
     * @return never {@literal null}.
     */
    protected byte[] serializeCacheKey(String cacheKey) {
        return ByteUtils.getBytes(cacheConfig.getKeySerializationPair().write(cacheKey));
    }

    /**
     * Serialize the value to cache.
     *
     * @param value must not be {@literal null}.
     * @return never {@literal null}.
     */
    protected byte[] serializeCacheValue(Object value) {

        if (isAllowNullValues() && value instanceof NullValue) {
            return BINARY_NULL_VALUE;
        }

        return ByteUtils.getBytes(cacheConfig.getValueSerializationPair().write(value));
    }

    /**
     * Return whether {@code null} values are allowed in this cache.
     */
    public final boolean isAllowNullValues() {
        return this.allowNullValues;
    }

    /**
     * Deserialize the given value to the actual cache value.
     *
     * @param value must not be {@literal null}.
     * @return can be {@literal null}.
     */
    @Nullable
    protected Object deserializeCacheValue(byte[] value) {

        if (isAllowNullValues() && ObjectUtils.nullSafeEquals(value, BINARY_NULL_VALUE)) {
            return NullValue.INSTANCE;
        }

        return cacheConfig.getValueSerializationPair().read(ByteBuffer.wrap(value));
    }

    /**
     * Customization hook for creating cache key before it gets serialized.
     *
     * @param key will never be {@literal null}.
     * @return never {@literal null}.
     */
    protected String createCacheKey(Object key) {

        String convertedKey = convertKey(key);

        if (!cacheConfig.usePrefix()) {
            return convertedKey;
        }

        return prefixCacheKey(convertedKey);
    }

    /**
     * Convert {@code key} to a {@link String} representation used for cache key creation.
     *
     * @param key will never be {@literal null}.
     * @return never {@literal null}.
     * @throws IllegalStateException if {@code key} cannot be converted to {@link String}.
     */
    protected String convertKey(Object key) {

        TypeDescriptor source = TypeDescriptor.forObject(key);
        if (conversionService.canConvert(source, TypeDescriptor.valueOf(String.class))) {
            return conversionService.convert(key, String.class);
        }

        Method toString = ReflectionUtils.findMethod(key.getClass(), "toString");

        if (toString != null && !Object.class.equals(toString.getDeclaringClass())) {
            return key.toString();
        }

        throw new IllegalStateException(
                String.format("Cannot convert %s to String. Register a Converter or override toString().", source));
    }

    private byte[] createAndConvertCacheKey(Object key) {
        return serializeCacheKey(createCacheKey(key));
    }

    private String prefixCacheKey(String key) {

        // allow contextual cache names by computing the key prefix on every call.
        return cacheConfig.getKeyPrefixFor(name) + key;
    }
}
