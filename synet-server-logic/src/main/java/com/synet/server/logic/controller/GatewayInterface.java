package com.synet.server.logic.controller;

import feign.RequestLine;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

public interface GatewayInterface {

    @RequestLine("POST /test")
    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,  headers = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    Mono<ByteBuffer> test(ByteBuffer body);
}
