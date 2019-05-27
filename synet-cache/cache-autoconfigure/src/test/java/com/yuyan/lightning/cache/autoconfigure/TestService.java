package com.yuyan.lightning.cache.autoconfigure;

import com.yuyan.lightning.cache.annotation.ReactiveCacheEvict;
import com.yuyan.lightning.cache.annotation.ReactiveCacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TestService {

    @ReactiveCacheable(value = "test", key = "#name")
    public Mono<TestBean> test(String name) {
        TestBean bean = new TestBean("thiistestbean");
        return Mono.just(bean);
    }

    @ReactiveCacheable(value = "test", key = "#name")
    public Mono<String> test3(String name) {
        return Mono.just("thiistestbeanformtest3");
    }

    @ReactiveCacheable(value = "test", key = "#name", sync = true)
    public Mono<String> test2(String name) {
        System.out.println(123);
        return Mono.defer(() -> {
            System.out.println("test cache is missing");
            return Mono.just("this is thiistestbean");
        });
    }

    @ReactiveCacheEvict(value = "test", key = "#name", beforeInvocation = true)
    public Mono<String> delete(String name) {
        return Mono.just("hello");
    }
}
