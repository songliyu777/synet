package com.synet.server.logic.Feign;

import com.netflix.client.ClientFactory;
import com.netflix.client.DefaultLoadBalancerRetryHandler;
import com.netflix.client.RetryHandler;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import feign.MethodMetadata;
import feign.Target;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import reactivefeign.ReactiveFeignBuilder;
import reactivefeign.cloud.CloudReactiveFeign;
import reactivefeign.cloud.LoadBalancerCommandFactory;
import reactivefeign.cloud.ReactiveFeignClientFactory;
import reactivefeign.publisher.PublisherClientFactory;
import reactivefeign.publisher.PublisherHttpClient;
import reactivefeign.spring.config.ReactiveFeignContext;

import java.net.URI;
import java.net.URISyntaxException;

import static reactivefeign.utils.FeignUtils.returnPublisherType;

/**
 * 继承 CloudReactiveFeign 实现消息的指定服务器转发
 */

@Slf4j
public class SynetReactiveFeign extends CloudReactiveFeign {


    public static <T> Builder<T> builder(ReactiveFeignBuilder<T> builder) {
        return new SynetBuilder<>(builder);
    }

    public static class SynetBuilder<T> extends CloudReactiveFeign.Builder<T> {

        private ReactiveFeignBuilder<T> builder;
        private SynetLoadBalancerCommandFactory loadBalancerCommandFactory = s -> null;

        protected SynetBuilder(ReactiveFeignBuilder<T> builder) {
            super(builder);
            this.builder = builder;
        }

        @Override
        public PublisherClientFactory buildReactiveClientFactory() {
            PublisherClientFactory publisherClientFactory = builder.buildReactiveClientFactory();
            return new PublisherClientFactory() {

                private Target target;

                @Override
                public void target(Target target) {
                    this.target = target;
                    publisherClientFactory.target(target);
                }

                @Override
                public PublisherHttpClient create(MethodMetadata methodMetadata) {
                    PublisherHttpClient publisherClient = publisherClientFactory.create(methodMetadata);
                    String serviceName = extractServiceName(target.url());
                    return new SynetPublisherClient(loadBalancerCommandFactory, serviceName,
                            publisherClient, returnPublisherType(methodMetadata));
                }
            };
        }

        private String extractServiceName(String url) {
            try {
                return new URI(url).getHost();
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Can't extract service name from url:" + url, e);
            }
        }

        @Autowired
        ReactiveFeignContext context;

        @Override
        public Builder<T> enableLoadBalancer(ReactiveFeignClientFactory clientFactory) {

            this.loadBalancerCommandFactory = serviceName -> {
                SpringClientFactory springClientFactory = context.getInstance(serviceName, SpringClientFactory.class);

                IClientConfig clientConfig;
                ILoadBalancer namedLoadBalancer;

                if (springClientFactory != null) {
                    clientConfig = springClientFactory.getClientConfig(serviceName);
                    namedLoadBalancer = springClientFactory.getLoadBalancer(serviceName);
                } else {
                    clientConfig = DefaultClientConfigImpl.getClientConfigWithDefaultValues(serviceName);
                    namedLoadBalancer = ClientFactory.getNamedLoadBalancer(serviceName);
                }

                RetryHandler retryHandler = getOrInstantiateRetryHandler(context, serviceName, clientConfig);

                return SynetLoadBalancerCommand.builder()
                        .withLoadBalancer(namedLoadBalancer)
                        .withRetryHandler(retryHandler)
                        .withClientConfig(clientConfig)
                        .build();
            };
            return this;
        }

        private RetryHandler getOrInstantiateRetryHandler(ReactiveFeignContext context, String clientName, IClientConfig clientConfig) {
            RetryHandler retryHandler = context.getInstance(clientName, RetryHandler.class);
            if(retryHandler == null){
                retryHandler = new DefaultLoadBalancerRetryHandler(clientConfig);
            }
            return retryHandler;
        }
    }
}
