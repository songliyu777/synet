package com.synet.server.logic.controller;

import com.google.protobuf.Message;
import com.synet.net.http.ProtobufProtocolEncoder;
import com.synet.net.protobuf.mapping.ProtoHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@Service
public class GatewayDelegation {

    @Autowired
    ProtobufProtocolEncoder encoder;

    @Autowired
    GatewayInterface gatewayInterface;

    public Mono<ByteBuffer> query(ProtoHeader head, Message message, String remote){
        return gatewayInterface.test(encoder.encode(head,message), remote);
    }
}
