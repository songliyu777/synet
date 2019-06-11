package com.synet.starter.feign;

import com.google.protobuf.Message;
import com.synet.net.protobuf.mapping.ProtoHeader;
import com.synet.net.protocol.NetProtocol;
import com.synet.net.protocol.ProtocolHeadDefine;
import com.synet.starter.protobufservice.ProtobufServiceConfiguration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.Objects;

public interface SynetRemoteInterface {

    default Mono<ByteBuffer> query(ProtoHeader head, Message message, String remote) {
        return protocol(ProtobufServiceConfiguration.encoder.encode(head, message), remote);
    }


    /**
     * Gateway remote query
     * @param body
     * @param remote 127.0.0.1 or 127.0.0.1:9000
     * @return
     */
    @PostMapping(value = "/pb/protocol", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,  headers = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    Mono<ByteBuffer> protocol(ByteBuffer body, @RequestParam("remote") String remote);
}
