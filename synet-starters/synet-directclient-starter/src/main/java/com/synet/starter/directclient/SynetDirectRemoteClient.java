package com.synet.starter.directclient;

import com.google.protobuf.Message;
import com.synet.net.protobuf.mapping.ProtoHeader;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

public interface SynetDirectRemoteClient {

    Mono<ByteBuffer> query(int id_hashcode, ProtoHeader head, Message message);

    Flux<ByteBuffer> query(String name, ProtoHeader head, Message message);
}
