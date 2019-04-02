package com.synet.server.logic.Feign;

import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import org.reactivestreams.Publisher;
import reactivefeign.client.ReactiveHttpRequest;
import reactivefeign.cloud.LoadBalancerCommandFactory;
import reactivefeign.publisher.PublisherHttpClient;
import reactivefeign.utils.LazyInitialized;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.Observable;
import rx.RxReactiveStreams;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 继承 RibbonPublisherClient 实现指定服务器调用
 */

public class SynetPublisherClient implements PublisherHttpClient {

    private final LazyInitialized<SynetLoadBalancerCommand<Object>> loadBalancerCommand;
    private final PublisherHttpClient publisherClient;
    private final Type publisherType;

    public SynetPublisherClient(SynetLoadBalancerCommandFactory loadBalancerCommandFactory,
                                String serviceName,
                                PublisherHttpClient publisherClient,
                                Type publisherType) {
        this.loadBalancerCommand = new LazyInitialized<>(() -> loadBalancerCommandFactory.apply(serviceName));
        this.publisherClient = publisherClient;
        this.publisherType = publisherType;
    }

    @Override
    public Publisher<Object> executeRequest(ReactiveHttpRequest request) {

//        if (request.uri().getQuery().startsWith("remote=")) {
//            String[] host_port = request.uri().getQuery().substring(7, request.uri().getQuery().length()).split(":");
//            ReactiveHttpRequest lbRequest = loadBalanceRequest(request, host_port[0], host_port[1]);
//            return publisherClient.executeRequest(lbRequest);
//        }

        SynetLoadBalancerCommand<Object> loadBalancerCommand = this.loadBalancerCommand.get();
        if (loadBalancerCommand != null) {
            Observable<?> observable = loadBalancerCommand.submit(server -> {

                ReactiveHttpRequest lbRequest = loadBalanceRequest(request, server);

                Publisher<Object> publisher = publisherClient.executeRequest(lbRequest);
                return RxReactiveStreams.toObservable(publisher);
            });

            Publisher<?> publisher = RxReactiveStreams.toPublisher(observable);

            if (publisherType == Mono.class) {
                return Mono.from(publisher);
            } else if (publisherType == Flux.class) {
                return Flux.from(publisher);
            } else {
                throw new IllegalArgumentException("Unknown publisherType: " + publisherType);
            }
        } else {
            return publisherClient.executeRequest(request);
        }
    }

    protected ReactiveHttpRequest loadBalanceRequest(ReactiveHttpRequest request, Server server) {
        URI uri = request.uri();
        try {
            URI lbUrl = new URI(uri.getScheme(), uri.getUserInfo(), server.getHost(), server.getPort(),
                    uri.getPath(), uri.getQuery(), uri.getFragment());
            return new ReactiveHttpRequest(request.method(), lbUrl, request.headers(), request.body());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected ReactiveHttpRequest loadBalanceRequest(ReactiveHttpRequest request, String host, String port) {
        URI uri = request.uri();
        try {
            URI lbUrl = new URI(uri.getScheme(), uri.getUserInfo(), host, Integer.valueOf(port),
                    uri.getPath(), uri.getQuery(), uri.getFragment());
            return new ReactiveHttpRequest(request.method(), lbUrl, request.headers(), request.body());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
