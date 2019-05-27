package com.yuyan.lightning.cache;


import com.yuyan.lightning.cache.concurrent.ConcurrentMapCache;
import org.junit.Test;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentMapCacheTests {

    @Test
    public void testNullValue() throws InterruptedException {
        Mono.justOrEmpty(null).subscribe(System.out::println);
        Mono.justOrEmpty(Optional.ofNullable(null)).subscribe(value -> System.out.println("this is value :" + value));
        Mono.fromCallable(() -> null).subscribe(value -> System.out.println("this is value :" + value));
        Mono.fromCallable(() -> null).switchIfEmpty(Mono.just("123")).subscribe(value -> System.out.println("this is value :" + value));
        Thread.sleep(1000L);
    }

    @Test
    public void testException() throws InterruptedException {
       Mono.defer(() -> Mono.error(new RuntimeException("this is runtime exception"))).doOnError(System.out::println).subscribe(value -> System.out.println("this is value :" + value));
    }

    @Test
    public void testCreateConcurrentMapCache() {
        ConcurrentMapCache cache = new ConcurrentMapCache("test");
        Mono<String> objectMono = cache.get("123", String.class);
        objectMono.switchIfEmpty(Mono.just("cache missed")).subscribe(value -> {
            System.out.println("subscribe value : " + value);
        });
    }

    @Test
    public void testCreateConcurrentMapCacheExsits() {
        ConcurrentMap<Object, Object> store = new ConcurrentHashMap();
        store.put("123", "123value");
        ConcurrentMapCache cache = new ConcurrentMapCache("test", store);
        Mono<String> objectMono = cache.get("123", String.class);
        objectMono.switchIfEmpty(Mono.just("cache missed")).subscribe(value -> {
            System.out.println("subscribe value : " + value);
        });
    }

    @Test
    public void testCreateConcurrentMapCacheClassTypeNotMatch() {
        ConcurrentMap<Object, Object> store = new ConcurrentHashMap();
        store.put("123", "123value");
        ConcurrentMapCache cache = new ConcurrentMapCache("test", store);
        Mono<TestObject> objectMono = cache.get("123", TestObject.class);
        objectMono.doOnError(System.out::println).subscribe(value -> {
            System.out.println("subscribe value : " + value);
        }, System.out::println );
    }

    @Test
    public void testCreateConcurrentMapCacheMissAndCache() {
        TestObject caching = new TestObject();
        caching.setId(123L);

        ConcurrentMapCache cache = new ConcurrentMapCache("test");
        Mono<TestObject> objectMono = cache.get("123", Mono.just(caching));
        objectMono.doOnError(System.out::println).subscribe(value -> {
            System.out.println("subscribe value : " + value);
        }, System.out::println );
    }

    class TestObject {
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
