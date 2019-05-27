import com.yuyan.lightning.cache.redis.DefaultReactiveRedisCacheWriter;
import com.yuyan.lightning.cache.redis.ReactiveRedisCache;
import org.junit.Test;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class ReactiveRedisCacheTests {

    private final static String HOST = "192.168.99.230";

    @Test
    public void testReactiveRedisConnection() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(HOST);
        LettuceConnectionFactory reactiveRedisConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        reactiveRedisConnectionFactory.afterPropertiesSet();

        System.out.println(reactiveRedisConnectionFactory);
    }

    @Test
    public void testRedisCacheConfiguration() {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        System.out.println(cacheConfiguration);
    }

    @Test
    public void testCreateReactiveRedisCache() {
        cache();
    }

    private ReactiveRedisCache cache() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(HOST);
        LettuceConnectionFactory reactiveRedisConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration);
        reactiveRedisConnectionFactory.afterPropertiesSet();

        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();

        ReactiveRedisCache cache = new ReactiveRedisCache("test", new DefaultReactiveRedisCacheWriter(reactiveRedisConnectionFactory), cacheConfiguration);
        System.out.println(cache);
        return cache;
    }

    @Test
    public void testGetValueFromReactiveRedisCacheButNotExists() {
        ReactiveRedisCache cache = cache();

        //测试key
        String key = "test";

        //先清理掉数据,确保可以不存在
        Mono<Boolean> clear = cache.clear();
        StepVerifier.create(clear)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        String notFound = "this is not found";
        Mono<String> test = cache.get(key, String.class);
        Mono<String> stringMono = test.switchIfEmpty(Mono.just(notFound));

        StepVerifier.create(stringMono)
                .consumeNextWith(System.out::println)
                .verifyComplete();
    }

    @Test
    public void testGetValueFromReactiveRedisCacheButNotExistsAndNoSwitchIfEmpty() {
        ReactiveRedisCache cache = cache();

        //测试key
        String key = "test";

        //先清理掉数据,确保可以不存在
        Mono<Boolean> clear = cache.clear();
        StepVerifier.create(clear)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        Mono<String> test = cache.get(key, String.class);

        // this test just send complete event , you cannot consume or subscription
        StepVerifier.create(test)
                .expectComplete()
                .verify();
    }

    @Test
    public void testGetValueFromReactiveRedisCacheButNotExistsAndValueLoader() {
        ReactiveRedisCache cache = cache();

        //测试key
        String key = "test";

        //先清理掉数据,确保可以不存在
        Mono<Boolean> clear = cache.clear();
        StepVerifier.create(clear)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        String valueLoaderValue = "testGetValueFromReactiveRedisCacheButNotExistsAndValueLoader";
        Mono<String> valueLoader = Mono.just(valueLoaderValue);
        Mono<String> test = cache.get(key, valueLoader);

        StepVerifier.create(test)
                .expectNext(valueLoaderValue)
                .verifyComplete();
    }

    @Test
    public void testGetValueFromReactiveRedisCacheAndExists() {
        ReactiveRedisCache cache = cache();

        //测试key
        String key = "test";

        //先清理掉数据,确保可以不存在
        Mono<Boolean> clear = cache.clear();
        StepVerifier.create(clear)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        //存入测试数据
        String testValue = "testGetValueFromReactiveRedisCacheAndExists";
        Mono<Boolean> put = cache.put(key, testValue);
        StepVerifier.create(put)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        //验证获取
        Mono<String> stringMono = cache.get(key, String.class);
        StepVerifier.create(stringMono)
                .expectNext(testValue)
                .verifyComplete();
    }


    @Test
    public void testPutValueInReactiveRedisCacheButNotExists() {
        ReactiveRedisCache cache = cache();

        //测试key
        String key = "test";

        //先清理掉数据,确保可以不存在
        Mono<Boolean> clear = cache.clear();
        StepVerifier.create(clear)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        //存入测试数据
        String testValue = "testPutValueInReactiveRedisCacheButNotExists";
        Mono<Boolean> put = cache.put(key, testValue);
        StepVerifier.create(put)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        //验证获取
        Mono<String> stringMono = cache.get(key, String.class);
        StepVerifier.create(stringMono)
                .expectNext(testValue)
                .verifyComplete();
    }

    @Test
    public void testPutValueInReactiveRedisCacheAndExists() {
        ReactiveRedisCache cache = cache();

        //测试key
        String key = "test";

        //先清理掉数据,确保可以不存在
        Mono<Boolean> clear = cache.clear();
        StepVerifier.create(clear)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        //存入测试数据
        String testValue = "testPutValueInReactiveRedisCacheButNotExists";
        Mono<Boolean> put = cache.put(key, testValue);
        StepVerifier.create(put)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        //存入测试数据
        String testValue2 = "testPutValueInReactiveRedisCacheAndExists";
        Mono<Boolean> put2 = cache.put(key, testValue2);
        StepVerifier.create(put2)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        //验证获取
        Mono<String> stringMono = cache.get(key, String.class);
        StepVerifier.create(stringMono)
                .expectNext(testValue2)
                .verifyComplete();
    }

    @Test
    public void testDeleteNotExistes() {
        ReactiveRedisCache cache = cache();
        Mono<Boolean> evict = cache.evict("noexiteskey");
        StepVerifier.create(evict)
                .expectNext(Boolean.TRUE)
                .verifyComplete();
    }

    @Test
    public void testDeteleExsitsKey() {
        ReactiveRedisCache cache = cache();
        Mono<Boolean> put = cache.put("exiteskey", "testDeteleExsitsKeyValue");
        Mono<Boolean> exitesMono = put.flatMap(item -> cache.evict("exiteskey"));
        StepVerifier.create(exitesMono)
                .expectNext(Boolean.TRUE)
                .verifyComplete();
    }

    @Test
    public void testClean() {
        ReactiveRedisCache cache = cache();
        Mono<Boolean> clear = cache.clear();
        StepVerifier.create(clear)
                .expectNext(Boolean.TRUE)
                .verifyComplete();
    }
}
