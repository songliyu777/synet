package com.synet.server.gateway.service;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.synet.TcpNetServer;
import com.synet.protocol.TcpNetProtocol;
import com.synet.server.gateway.feign.MessageClient;
import com.synet.session.ISession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactivefeign.cloud.CloudReactiveFeign;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

@Slf4j
@Service
public class TcpNetService {

    @Autowired
    MessageClient messageClient;

    TcpNetServer server;

    Consumer<TcpNetProtocol> process = protocol -> {

        Mono<ByteBuffer> buf = messageClient.test(ByteBuffer.wrap(protocol.toArray()));
        buf.map((b) -> TcpNetProtocol.create(b)).subscribe(t -> {
            server.send(t.getHead().getSession(), t.toArray(), () -> t.release());
            protocol.release();
        }, (e) -> {
            e.printStackTrace();
            protocol.release();
        });

    };
    Consumer<Throwable> error = error -> error.printStackTrace();
    Consumer<? super ISession> doOnConnection = session -> {

        //TcpNetProtocol.create(ProtocolHeadDefine.ENCRYPT_PROTOBUF, ProtocolHeadDefine.VERSION, 0, 0, (short) 0, null, 0);
    };

    public TcpNetService() throws Exception {

//        DefaultClientConfigImpl clientConfig = new DefaultClientConfigImpl();
//        clientConfig.loadDefaultValues();
//        clientConfig.setProperty(CommonClientConfigKey.NFLoadBalancerClassName, BaseLoadBalancer.class.getName());
//        ILoadBalancer lb = ClientFactory.registerNamedLoadBalancerFromclientConfig("server-logic", clientConfig);
//        lb.addServers(asList(new Server("localhost", 8000)));


//        feignclient = CloudReactiveFeign.<MessageClient>builder()
//                .enableLoadBalancer()
//                .setHystrixCommandSetterFactory(getHystrixCommandSetterFactory())
//                .target(MessageClient.class, "http://server-logic");


        server = new TcpNetServer("", 7000, 60000, 120000);
        server.setProcessHandler(process);
        server.setErrorHandler(error);
        server.doOnConnection(doOnConnection);
        server.createServer();
    }

    private CloudReactiveFeign.SetterFactory getHystrixCommandSetterFactory() {
        return (target, methodMetadata) -> {
            String groupKey = target.name();
            HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey(methodMetadata.configKey());
            return HystrixObservableCommand.Setter
                    .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
                    .andCommandKey(commandKey)
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                                    .withExecutionIsolationSemaphoreMaxConcurrentRequests(1000)
                                    .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
//                            .withCircuitBreakerRequestVolumeThreshold(0)
//                            .withExecutionTimeoutEnabled(false)

//                            .withCircuitBreakerSleepWindowInMilliseconds(SLEEP_WINDOW)
//                            .withMetricsHealthSnapshotIntervalInMilliseconds(10)
                    );
        };
    }
}
