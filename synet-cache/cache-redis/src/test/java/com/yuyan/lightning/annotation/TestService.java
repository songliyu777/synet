package com.yuyan.lightning.annotation;

import com.yuyan.lightning.cache.annotation.ReactiveCacheable;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestService {

    @ReactiveCacheable(value = "test", key = "#name")
    public Mono<TestBean> test(String name) {
        TestBean bean = new TestBean("thiistestbean");
        return Mono.just(bean);
    }

    @ReactiveCacheable(value = "test", key = "#name", sync = true)
    public Mono<String> test2(String name) {
        System.out.println(123);
        return Mono.defer(() -> {
            System.out.println(1234);
            return Mono.just("this is thiistestbean");
        });
    }

    @ReactiveCacheable(value = "test::list", key = "#name", sync = true)
    public Flux<TestBean> test3(String name) {
        List<String> beans = Lists.newArrayList("thiistestbean1", "thiistestbean2", "thiistestbean3");
        List<TestBean> testBeans = beans.stream().map(TestBean::new).collect(Collectors.toList());
        return Flux.fromIterable(testBeans);
    }

    @ReactiveCacheable(value = "test::list", key = "#name", sync = true)
    public Flux<TestBean> testEmpty(String name) {
        List<String> beans = Lists.newArrayList();
        List<TestBean> testBeans = beans.stream().map(TestBean::new).collect(Collectors.toList());
        return Flux.fromIterable(testBeans);
    }
}
