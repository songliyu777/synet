package com.synet.server.logic.Feign;

import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import feign.MethodMetadata;
import feign.Target;
import lombok.extern.slf4j.Slf4j;
import reactivefeign.ReactiveFeignBuilder;
import reactivefeign.cloud.CloudReactiveFeign;
import reactivefeign.cloud.LoadBalancerCommandFactory;
import reactivefeign.cloud.ReactiveFeignClientFactory;
import reactivefeign.publisher.PublisherClientFactory;
import reactivefeign.publisher.PublisherHttpClient;

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
        private LoadBalancerCommandFactory loadBalancerCommandFactory = s -> null;
        private SynetLoadBalancerComandFactory synetLoadBalancerCommandFactory = s -> null;

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

        @Override
        public Builder<T> setLoadBalancerCommandFactory(LoadBalancerCommandFactory loadBalancerCommandFactory) {
            super.setLoadBalancerCommandFactory(loadBalancerCommandFactory);
            this.loadBalancerCommandFactory = loadBalancerCommandFactory;
            return this;
        }

        private String extractServiceName(String url) {
            try {
                return new URI(url).getHost();
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Can't extract service name from url:" + url, e);
            }
        }

        @Override
        public Builder<T> enableLoadBalancer(ReactiveFeignClientFactory clientFactory){
            return setLoadBalancerCommandFactory(serviceName ->
                    LoadBalancerCommand.builder()
                            .withLoadBalancer(clientFactory.loadBalancer(serviceName))
                            .withClientConfig(clientFactory.clientConfig(serviceName))
                            .build());
        }

//        @Override
//        public Builder<T> enableLoadBalancer(ReactiveFeignClientFactory clientFactory) {
//            synetLoadBalancerCommandFactory = serviceName -> {
//                return SynetLoadBalancerCommand.builder()
//                        .withLoadBalancer(clientFactory.loadBalancer(serviceName))
//                        .withClientConfig(clientFactory.clientConfig(serviceName))
//                        .build();
//            };
//            return this;
//        }
    }
}
