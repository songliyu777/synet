package com.synet.starter.directclient;

import com.google.protobuf.Message;
import com.synet.net.protobuf.mapping.ProtoHeader;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

public interface DirectClient {

    int getId();

    Mono<ByteBuffer> query(ProtoHeader head, Message message);
}
