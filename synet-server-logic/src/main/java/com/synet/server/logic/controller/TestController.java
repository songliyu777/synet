package com.synet.server.logic.controller;

import com.synet.net.protobuf.mapping.*;
import com.synet.protobuf.protocol.Syprotocol;
import reactor.core.publisher.Mono;

@ProtobufController
public class TestController {

//    @Autowired
//    RemoteDelegation remoteDelegation;

    @ProtobufMapping(cmd = (short)Syprotocol.protocol_id.login_msg_VALUE)
    public Mono<ProtoResponse> test(@Header ProtoHeader head, @Body Syprotocol.cts_Login test) {
        ProtoResponse response = ProtoResponse.builder().protoHeader(head).message(test).build();
        return Mono.just(response);

        //remoteDelegation.query(head, test, head.getRemoteAddress()).subscribe();
        //return Mono.just(response);
        //return Mono.just(response).doOnSuccess((r) -> remoteDelegation.query(head, test, head.getRemoteAddress()).subscribe());
    }

}