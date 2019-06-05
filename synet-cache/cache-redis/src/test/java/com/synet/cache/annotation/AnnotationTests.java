package com.synet.cache.annotation;

import com.synet.cache.interceptor.CacheOperation;
import com.synet.cache.interceptor.CacheOperationSource;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AnnotationTests {

    @Autowired
    private TestService testService;

    @Autowired
    private CacheOperationSource cacheOperationSource;

    @Test
    public void testCacheOperationSource() {
        Method test = ReflectionUtils.findMethod(TestService.class, "test", String.class);
        System.out.println(test);
        Collection<CacheOperation> cacheOperations = cacheOperationSource.getCacheOperations(test, TestService.class);
        System.out.println(cacheOperations);
    }

    @Test
    public void testOne() {
        String testValue = "this is thiistestbean";
        Mono<String> test = testService.test2("123");
        StepVerifier.create(test)
                .expectNext(testValue)
                .verifyComplete();
    }

    @Test
    public void testSaveFlux() {
        List<String> beans = Lists.newArrayList("thiistestbean1", "thiistestbean2", "thiistestbean3");
        List<TestBean> testBeans = beans.stream().map(TestBean::new).collect(Collectors.toList());
        Flux<TestBean> testBeanFlux = testService.test3("123");
        StepVerifier.create(testBeanFlux)
                .expectNextSequence(testBeans)
                .verifyComplete();
    }

    @Test
    public void testEmptyFlux() {
        Flux<TestBean> testBeanFlux = testService.testEmpty("empty");
        Flux<TestBean> empty = testBeanFlux.switchIfEmpty(Mono.just(new TestBean("empty")));
        StepVerifier.create(empty)
                .expectNext(new TestBean("empty"))
                .verifyComplete();
    }
}
