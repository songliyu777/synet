package com.synet.server.logic.controller;

import feign.Body;
import feign.Param;
import feign.RequestLine;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;


//public interface GatewayInterface {
//
//    @RequestLine("POST /test")
//    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,  headers = MediaType.APPLICATION_OCTET_STREAM_VALUE)
//    Mono<Void> test(ByteBuffer body);
//}

@ReactiveFeignClient(name = "server-gateway")
public interface GatewayInterface {

    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,  headers = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    Mono<ByteBuffer> test(ByteBuffer body, @RequestParam("ip") String ip);
}

