package com.synet.server.logic.config;

import com.synet.server.logic.Feign.SynetReactiveFeign;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import reactivefeign.ReactiveFeignBuilder;
import reactivefeign.cloud.CloudReactiveFeign;
import reactivefeign.spring.config.ReactiveFeignRibbonConfigurator;

@Configuration
public class FeignClientsConfiguration {

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
        if(enableLoadBalancer){
            cloudBuilder = cloudBuilder.enableLoadBalancer();
        }
        if(!enableHystrix){
            cloudBuilder = cloudBuilder.disableHystrix();
        }
        return cloudBuilder;
    }

}
