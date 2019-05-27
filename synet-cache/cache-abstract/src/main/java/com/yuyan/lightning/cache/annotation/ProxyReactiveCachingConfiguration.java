package com.yuyan.lightning.cache.annotation;

import com.yuyan.lightning.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import com.yuyan.lightning.cache.interceptor.CacheInterceptor;
import com.yuyan.lightning.cache.interceptor.CacheOperationSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.cache.config.CacheManagementConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyReactiveCachingConfiguration extends AbstractCachingConfiguration {

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

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CacheInterceptor cacheInterceptor() {
       CacheInterceptor interceptor = new CacheInterceptor();
       interceptor.configure(this.keyGenerator, this.cacheResolver, this.cacheManager);
       interceptor.setCacheOperationSource(cacheOperationSource());
       return interceptor;
    }
}
