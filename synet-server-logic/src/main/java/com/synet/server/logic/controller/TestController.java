package com.synet.server.logic.controller;

import com.synet.net.protobuf.mapping.*;
import com.synet.protobuf.TestOuterClass;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@ProtobufController
public class TestController {

    @Autowired
    GatewayInterface gatewayInterface;

    @ProtobufMapping(cmd = 1)
    public Mono<ProtoResponse> test(@Header ProtoHeader head, @Body TestOuterClass.Test test) {

        ProtoResponse response = ProtoResponse.builder().protoHeader(head).message(test).build();

        gatewayInterface.test(ByteBuffer.wrap(test.toByteArray())).subscribe();

        return Mono.just(response);
    }
}