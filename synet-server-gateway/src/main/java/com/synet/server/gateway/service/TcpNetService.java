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
import reactor.netty.tcp.InetSocketAddressUtil;

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
            System.err.println(e);
            protocol.release();
        });

    };
    Consumer<Throwable> error = error -> System.err.println(error);
    Consumer<? super ISession> doOnConnection = session -> {

        //TcpNetProtocol.create(ProtocolHeadDefine.ENCRYPT_PROTOBUF, ProtocolHeadDefine.VERSION, 0, 0, (short) 0, null, 0);
    };

    public TcpNetService() throws Exception {
        server = new TcpNetServer("", 7000, 60000, 120000);
        server.setProcessHandler(process);
        server.setErrorHandler(error);
        server.doOnConnection(doOnConnection);
        server.createServer();
    }

    public TcpNetServer GetServer() {
        return server;
    }

}
