package com.synet.starter.feign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import reactivefeign.ReactiveFeignBuilder;
import reactivefeign.cloud.CloudReactiveFeign;
import reactivefeign.webclient.WebClientFeignCustomizer;
import reactivefeign.webclient.WebReactiveFeign;

@Configuration
public class FeignClientsConfiguration {

    @Bean
    @Scope("prototype")
    @ConditionalOnClass(WebReactiveFeign.class)
    @ConditionalOnMissingBean(ignoredType = "reactivefeign.cloud.CloudReactiveFeign.Builder")
    public ReactiveFeignBuilder reactiveFeignBuilder(@Autowired(required = false) WebClientFeignCustomizer webClientCustomizer) {
        return webClientCustomizer != null
                ? WebReactiveFeign.builder(webClientCustomizer)
                : WebReactiveFeign.builder();
    }

    @Bean
    @Primary
    @Scope("prototype")
    public CloudReactiveFeign.Builder reactiveFeignCloudBuilder(
            ReactiveFeignBuilder reactiveFeignBuilder,
            @Value("${reactive.feign.hystrix.enabled:true}")
                    boolean enableHystrix,
            @Value("${reactive.feign.ribbon.enabled:true}")
                    boolean enableLoadBalancer) {
        SynetReactiveFeign.Builder cloudBuilder = SynetReactiveFeign.builder(reactiveFeignBuilder);
        if (enableLoadBalancer) {
            cloudBuilder = cloudBuilder.enableLoadBalancer();
        }
        if (!enableHystrix) {
            cloudBuilder = cloudBuilder.disableHystrix();
        }
        return cloudBuilder;
    }

}
