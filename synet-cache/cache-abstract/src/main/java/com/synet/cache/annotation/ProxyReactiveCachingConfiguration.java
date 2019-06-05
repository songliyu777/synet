package com.synet.cache.annotation;

import com.synet.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import com.synet.cache.interceptor.CacheInterceptor;
import com.synet.cache.interceptor.CacheOperationSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.config.CacheManagementConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * 原理说明
 * AnnotationCacheOperationSource 的主要作用是获取定义在类和方法上的 SpringCache 相关的标注并将其转换为对应的 CacheOperation 属性。
 * BeanFactoryCacheOperationSourceAdvisor 是一个PointcutAdvisor，是SpringCache使用Spring AOP机制的关键所在，该advisor 会织入到需要执行缓存操作的bean的增强代理中形成一个切面。并在方法调用时在该切面上执行拦截器CacheInterceptor的业务逻辑。
 * CacheInterceptor是一个拦截器，当方法调用时碰到了BeanFactoryCacheOperationSourceAdvisor定义的切面，就会执行CacheInterceptor的业务逻辑，该业务逻辑就是缓存的核心业务逻辑。
 *
 * 从Spring的AOP机制已知，要对一个方法或类切入需要实现如下：
 * 一个Advisor，它可以扩展Spring中AbstractBeanFactoryPointcutAdvisor
 * 一个Pointcut，它可以扩展Spring中StaticMethodMatcherPointcut
 * 一个MethodInterceptor，在此接口中实现拦截逻辑
 */

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyReactiveCachingConfiguration extends AbstractCachingConfiguration {

    /**
     * BeanFactoryCacheOperationSourceAdvisor 中包含了成员变量 CacheOperationSourcePointcut
     * Pointcut，它可以扩展Spring中StaticMethodMatcherPointcut
     * BeanFactoryCacheOperationSourceAdvisor 自身继承了 Advisor
     * */

    @Bean(name = CacheManagementConfigUtils.CACHE_ADVISOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public BeanFactoryCacheOperationSourceAdvisor cacheAdvisor() {
        BeanFactoryCacheOperationSourceAdvisor advisor = new BeanFactoryCacheOperationSourceAdvisor();
        advisor.setCacheOperationSource(cacheOperationSource());
        advisor.setAdvice(cacheInterceptor());
        if (this.enableCaching != null) {
            advisor.setOrder(this.enableCaching.<Integer>getNumber("order"));
        }
        return advisor;
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CacheOperationSource cacheOperationSource() {
        return new AnnotationReactiveCacheOperationSource();
    }

    /**
     * CacheInterceptor implements MethodInterceptor，在此接口中实现拦截逻辑
     * */

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CacheInterceptor cacheInterceptor() {
       CacheInterceptor interceptor = new CacheInterceptor();
       interceptor.configure(this.keyGenerator, this.cacheResolver, this.cacheManager);
       interceptor.setCacheOperationSource(cacheOperationSource());
       return interceptor;
    }
}
