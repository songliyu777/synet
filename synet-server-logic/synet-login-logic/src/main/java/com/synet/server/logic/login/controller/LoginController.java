package com.synet.server.logic.login.controller;

import com.synet.net.protobuf.mapping.*;
import com.synet.protobuf.protocol.Syprotocol;
import com.synet.server.logic.login.remote.GatewayInterface;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

@ProtobufController
public class LoginController {

    @Autowired
    GatewayInterface gatewayInterface;

    @ProtobufMapping(cmd = (short) Syprotocol.protocol_id.login_msg_VALUE)
    public Mono<ProtoResponse> login(@Header ProtoHeader head, @Body Syprotocol.cts_Login cts) {
        ProtoResponse response = ProtoResponse.builder().protoHeader(head).message(cts).build();
        //return Mono.just(response);

        //gatewayInterface.query(head, test, head.getRemoteAddress()).subscribe();
        //return Mono.just(response);
        return Mono.just(response).doOnSuccess((r) -> gatewayInterface.query(head, cts, head.getRemoteAddress()).subscribe());
    }
}
