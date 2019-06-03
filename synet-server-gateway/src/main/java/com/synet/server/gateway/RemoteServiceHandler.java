package com.synet.server.gateway;

import com.synet.net.tcp.TcpServiceHandler;
import com.synet.server.gateway.feign.MessageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@Service
public class RemoteServiceHandler{ //extends TcpServiceHandler {

    @Autowired
    MessageClient messageClient;


    public Mono<ByteBuffer> invoke(ByteBuffer byteBuffer) {
        return messageClient.protocol(byteBuffer);
    }

}