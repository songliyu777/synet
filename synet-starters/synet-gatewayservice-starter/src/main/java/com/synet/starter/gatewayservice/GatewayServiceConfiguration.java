package com.synet.starter.gatewayservice;

import com.netflix.client.ClientFactory;
import com.netflix.client.DefaultLoadBalancerRetryHandler;
import com.netflix.client.RetryHandler;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import com.synet.net.route.DefaultRouteMatcher;
import com.synet.net.route.RouteDefinition;
import com.synet.net.route.RouteMatcher;
import com.synet.starter.gatewayservice.controller.PostController;
import com.synet.starter.gatewayservice.handler.RemoteInvokeHandler;
import com.synet.starter.gatewayservice.service.LightningServiceLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.reactive.WebClientCustomizer;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactivefeign.ReactiveFeignBuilder;
import reactivefeign.cloud.CloudReactiveFeign;
import reactivefeign.cloud.LoadBalancerCommandFactory;
import reactivefeign.spring.config.ReactiveFeignAutoConfiguration;
import reactivefeign.spring.config.ReactiveFeignClientsConfiguration;
import reactivefeign.spring.config.ReactiveFeignHystrixConfigurator;
import reactivefeign.spring.config.ReactiveFeignRibbonConfigurator;
import reactivefeign.webclient.WebClientFeignCustomizer;
import reactivefeign.webclient.WebReactiveFeign;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@AutoConfigureAfter({ReactiveFeignAutoConfiguration.class})
@EnableConfigurationProperties(value = GatewayServiceProperties.class)
public class GatewayServiceConfiguration {

    @Bean
    public RouteMatcher routeMatcher(GatewayServiceProperties properties) {
        List<RouteDefinition> routes = properties.getRoutes();
        return new DefaultRouteMatcher(routes);
    }

    @Bean
    public LightningServiceLocator lightningServiceLocator(RouteMatcher routeMatcher, CloudReactiveFeign.Builder builder) {
        return new LightningServiceLocator(routeMatcher.getRoutes(), builder);
    }

    @Bean
    @ConditionalOnMissingBean(value = RemoteInvokeHandler.class)
    public RemoteInvokeHandler serviceInvokeHandler(RouteMatcher routeMatcher, LightningServiceLocator lightningServiceLocator) {
        return new RemoteInvokeHandler(routeMatcher, lightningServiceLocator);
    }


    @Bean
    public RouterFunction<ServerResponse> routes(PostController postController) {
        return route(POST("/test").and(contentType(APPLICATION_OCTET_STREAM)), postController::test);
    }

    @Bean
    PostController postController() {
        return new PostController();
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnClass(WebReactiveFeign.class)
    @ConditionalOnMissingBean(ignoredType = "reactivefeign.cloud.CloudReactiveFeign.Builder")
    public ReactiveFeignBuilder reactiveFeignBuilder(@Autowired(required = false) WebClientFeignCustomizer webClientCustomizer) {
        return webClientCustomizer != null
                ? WebReactiveFeign.builder(webClientCustomizer)
                : WebReactiveFeign.builder();
    }

    @AutoConfigureAfter(GatewayServiceConfiguration.class)
    @Configuration
    @ConditionalOnClass({HystrixCommand.class, LoadBalancerCommand.class, CloudReactiveFeign.class})
    @ConditionalOnProperty(name = "reactive.feign.cloud.enabled", havingValue = "true", matchIfMissing = true)
    protected static class ReactiveFeignClientsCloudConfiguration {

        @Bean
        @Primary
        @Scope("prototype")
        @ConditionalOnMissingBean
        public reactivefeign.cloud.CloudReactiveFeign.Builder reactiveFeignCloudBuilder(ReactiveFeignBuilder reactiveFeignBuilder, @Value("${reactive.feign.hystrix.enabled:true}") boolean enableHystrix, @Value("${reactive.feign.ribbon.enabled:true}") boolean enableLoadBalancer, SpringClientFactory springClientFactory) {

            LoadBalancerCommandFactory balancerCommandFactory = serviceName -> {

                IClientConfig clientConfig;
                ILoadBalancer namedLoadBalancer;
                if(springClientFactory != null){
                    clientConfig = springClientFactory.getClientConfig(serviceName);
                    namedLoadBalancer = springClientFactory.getLoadBalancer(serviceName);
                } else {
                    clientConfig = DefaultClientConfigImpl.getClientConfigWithDefaultValues(serviceName);
                    namedLoadBalancer = ClientFactory.getNamedLoadBalancer(serviceName);
                }

                RetryHandler retryHandler = new DefaultLoadBalancerRetryHandler(clientConfig);

                return LoadBalancerCommand.builder()
                        .withLoadBalancer(namedLoadBalancer)
                        .withRetryHandler(retryHandler)
                        .withClientConfig(clientConfig)
                        .build();
            };

            CloudReactiveFeign.Builder cloudBuilder = CloudReactiveFeign.builder(reactiveFeignBuilder)
                    .setLoadBalancerCommandFactory(balancerCommandFactory);

            if (!enableHystrix) {
                cloudBuilder = cloudBuilder.disableHystrix();
            }
            return cloudBuilder;
        }
    }
}
