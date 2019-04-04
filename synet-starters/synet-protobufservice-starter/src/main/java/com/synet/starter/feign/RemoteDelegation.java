package com.synet.starter.feign;

import com.google.protobuf.Message;
import com.synet.net.http.ProtocolEncoder;
import com.synet.net.protobuf.mapping.ProtoHeader;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

public interface RemoteDelegation {

    default Mono<ByteBuffer> query(ProtoHeader head, Message message, String remote) {
        return getRemote().test(getEncoder().encode(head, message), remote);
    }

    ProtocolEncoder<Message> getEncoder();

    RemoteInterface getRemote();
}
