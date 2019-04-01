package com.synet.server.logic.Feign;

import reactivefeign.cloud.LoadBalancerCommandFactory;
import reactivefeign.cloud.publisher.RibbonPublisherClient;
import reactivefeign.publisher.PublisherHttpClient;

import java.lang.reflect.Type;

/**
 * 继承 RibbonPublisherClient 实现指定服务器调用
 */

public class SynetPublisherClient extends RibbonPublisherClient {
    public SynetPublisherClient(LoadBalancerCommandFactory loadBalancerCommandFactory,
                                String serviceName,
                                PublisherHttpClient publisherClient,
                                Type publisherType) {
        super(loadBalancerCommandFactory, serviceName, publisherClient, publisherType);
    }
}
