package com.synet.server.logic.controller;

import com.synet.net.protobuf.mapping.*;
import com.synet.protobuf.TestOuterClass;
import reactor.core.publisher.Mono;

@ProtobufController
public class TestController {

    @ProtobufMapping(cmd = 1)
    public Mono<ProtoResponse> test(@Header ProtoHeader head, @Body TestOuterClass.Test test) {

        ProtoResponse response = ProtoResponse.builder().protoHeader(head).message(test).build();

        return Mono.just(response);
    }
}