package com.synet.server.gateway.controller;


import com.google.protobuf.AbstractMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.synet.message.IMessage;
import com.synet.protobuf.TestOuterClass;
import com.synet.protocol.ProtocolHeadDefine;
import com.synet.protocol.TcpNetProtocol;
import com.synet.server.gateway.service.TcpNetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.function.Function;

@Component
public class PostHandler {

    @Autowired
    TcpNetService tcpNetService;

    Function<? super ByteBuffer, ? extends Mono<ServerResponse>> bufferToSend = (buffer) -> {
        long session = buffer.getLong(TcpNetProtocol.session_index);
        tcpNetService.GetServer().send(session, buffer);
        return ServerResponse.ok().build();
    };

    public Mono<ServerResponse> test(ServerRequest req) {
        return req.body(BodyExtractors.toMono(ByteBuffer.class)).flatMap(bufferToSend);
    }

}
