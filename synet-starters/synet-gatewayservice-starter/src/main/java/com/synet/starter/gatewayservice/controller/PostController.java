package com.synet.starter.gatewayservice.controller;


import com.synet.net.net.NetServive;
import com.synet.net.protocol.NetProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

public class PostController {

    @Autowired
    NetServive netServive;

    Mono<ServerResponse> bufferToSend(ByteBuffer byteBuffer) {
        long session = byteBuffer.getLong(NetProtocol.session_index);
        netServive.send(session, byteBuffer);
        return ServerResponse.ok().build();
    }

    public Mono<ServerResponse> test(ServerRequest req) {
        return req.body(BodyExtractors.toMono(ByteBuffer.class)).flatMap(this::bufferToSend);
    }

}
