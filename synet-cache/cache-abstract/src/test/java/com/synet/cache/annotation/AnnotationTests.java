package com.synet.cache.annotation;

import com.synet.cache.interceptor.CacheOperation;
import com.synet.cache.interceptor.CacheOperationSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Method;
import java.util.Collection;

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
}
