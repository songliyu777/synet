package com.synet.server.gateway.feign;

import feign.Headers;
import feign.RequestLine;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@ReactiveFeignClient(name = "server-logic")
public interface MessageClient {

//    @RequestLine("POST /test")
//    @Headers("Content-Type: " + MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,  headers = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    Mono<ByteBuffer> test(ByteBuffer body);
}
