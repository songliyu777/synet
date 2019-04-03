package com.synet.server.gateway.feign;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@ReactiveFeignClient(name = "server-logic")
public interface MessageClient {

    @PostMapping(value = "/pb/protocol", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,  headers = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    Mono<ByteBuffer> protocol(ByteBuffer body);
}
