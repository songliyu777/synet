package com.synet.cache.annotation;

import com.synet.cache.interceptor.CacheResolver;
import com.synet.cache.interceptor.KeyGenerator;
import com.synet.cache.ReactiveCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.lang.Nullable;

public interface ReactiveCachingConfigurer {

    /**
     * Return the cache manager bean to use for annotation-driven cache
     * management. A default {@link CacheResolver} will be initialized
     * behind the scenes with this cache manager. For more fine-grained
     * management of the cache resolution, consider setting the
     * {@link CacheResolver} directly.
     * <p>Implementations must explicitly declare
     * {@link org.springframework.context.annotation.Bean @Bean}, e.g.
     * <pre class="code">
     * &#064;Configuration
     * &#064;EnableCaching
     * public class AppConfig extends CachingConfigurerSupport {
     *     &#064;Bean // important!
     *     &#064;Override
     *     public CacheManager cacheManager() {
     *         // configure and return CacheManager instance
     *     }
     *     // ...
     * }
     * </pre>
     * See @{@link EnableCaching} for more complete examples.
     */
    @Nullable
    ReactiveCacheManager cacheManager();

    /**
     * Return the {@link CacheResolver} bean to use to resolve regular caches for
     * annotation-driven cache management. This is an alternative and more powerful
     * option of specifying the {@link CacheManager} to use.
     * <p>If both a {@link #cacheManager()} and {@code #cacheResolver()} are set,
     * the cache manager is ignored.
     * <p>Implementations must explicitly declare
     * {@link org.springframework.context.annotation.Bean @Bean}, e.g.
     * <pre class="code">
     * &#064;Configuration
     * &#064;EnableCaching
     * public class AppConfig extends CachingConfigurerSupport {
     *     &#064;Bean // important!
     *     &#064;Override
     *     public CacheResolver cacheResolver() {
     *         // configure and return CacheResolver instance
     *     }
     *     // ...
     * }
     * </pre>
     * See {@link EnableCaching} for more complete examples.
     */
    @Nullable
    CacheResolver cacheResolver();

    /**
     * Return the key generator bean to use for annotation-driven cache management.
     * Implementations must explicitly declare
     * {@link org.springframework.context.annotation.Bean @Bean}, e.g.
     * <pre class="code">
     * &#064;Configuration
     * &#064;EnableCaching
     * public class AppConfig extends CachingConfigurerSupport {
     *     &#064;Bean // important!
     *     &#064;Override
     *     public KeyGenerator keyGenerator() {
     *         // configure and return KeyGenerator instance
     *     }
     *     // ...
     * }
     * </pre>
     * See @{@link EnableCaching} for more complete examples.
     */
    @Nullable
    KeyGenerator keyGenerator();

    /**
     * Return the {@link CacheErrorHandler} to use to handle cache-related errors.
     * <p>By default,{@link org.springframework.cache.interceptor.SimpleCacheErrorHandler}
     * is used and simply throws the exception back at the client.
     * <p>Implementations must explicitly declare
     * {@link org.springframework.context.annotation.Bean @Bean}, e.g.
     * <pre class="code">
     * &#064;Configuration
     * &#064;EnableCaching
     * public class AppConfig extends CachingConfigurerSupport {
     *     &#064;Bean // important!
     *     &#064;Override
     *     public CacheErrorHandler errorHandler() {
     *         // configure and return CacheErrorHandler instance
     *     }
     *     // ...
     * }
     * </pre>
     * See @{@link EnableCaching} for more complete examples.
     */
    @Nullable
    CacheErrorHandler errorHandler();
}
