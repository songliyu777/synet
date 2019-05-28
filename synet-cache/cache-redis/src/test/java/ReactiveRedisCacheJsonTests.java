import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yuyan.lightning.cache.redis.DefaultReactiveRedisCacheWriter;
import com.yuyan.lightning.cache.redis.ReactiveRedisCache;
import org.junit.Test;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

public class ReactiveRedisCacheJsonTests {

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
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(objectMapper);
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        cacheConfiguration.serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(serializer));
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

        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(objectMapper);
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        cacheConfiguration = cacheConfiguration.serializeValuesWith(RedisSerializationContext.SerializationPair
                .fromSerializer(serializer));

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

        TestBean notFound = TestBean.create("this is not found");
        Mono<TestBean> test = cache.get(key, TestBean.class);
        Mono<TestBean> stringMono = test.switchIfEmpty(Mono.just(notFound));

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

        Mono<TestBean> test = cache.get(key, TestBean.class);

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

        TestBean valueLoaderValue = TestBean.create("testGetValueFromReactiveRedisCacheButNotExistsAndValueLoader");
        Mono<TestBean> valueLoader = Mono.just(valueLoaderValue);
        Mono<TestBean> test = cache.get(key, valueLoader);

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
        TestBean testValue = TestBean.create("testGetValueFromReactiveRedisCacheAndExists");
        Mono<Boolean> put = cache.put(key, testValue);
        StepVerifier.create(put)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        //验证获取
        Mono<TestBean> stringMono = cache.get(key, TestBean.class);
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
        TestBean testValue = TestBean.create("testPutValueInReactiveRedisCacheButNotExists");
        Mono<Boolean> put = cache.put(key, testValue);
        StepVerifier.create(put)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        //验证获取
        Mono<TestBean> stringMono = cache.get(key, TestBean.class);
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
        TestBean testValue = TestBean.create("testPutValueInReactiveRedisCacheButNotExists");
        Mono<Boolean> put = cache.put(key, testValue);
        StepVerifier.create(put)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        //存入测试数据
        TestBean testValue2 = TestBean.create("testPutValueInReactiveRedisCacheAndExists");
        Mono<Boolean> put2 = cache.put(key, testValue2);
        StepVerifier.create(put2)
                .expectNext(Boolean.TRUE)
                .verifyComplete();

        //验证获取
        Mono<TestBean> stringMono = cache.get(key, TestBean.class);
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

    static class TestBean {

        private String name;

        public TestBean() {
        }

        public TestBean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "TestBean{" +
                    "name='" + name + '\'' +
                    '}';
        }

        public static TestBean create(String name) {
            return new TestBean(name);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestBean testBean = (TestBean) o;
            return Objects.equals(name, testBean.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}
