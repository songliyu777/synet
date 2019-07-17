package com.synet.cache.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ReactiveCaching {

    ReactiveCacheable[] cacheable() default {};

    ReactiveCachePut[] put() default {};

    ReactiveCacheEvict[] evict() default {};
}
