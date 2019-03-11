package com.synet.server.gateway.feign;

import feign.Headers;
import feign.RequestLine;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@Headers({"Accept: " + MediaType.APPLICATION_OCTET_STREAM_VALUE})
public interface MessageClient {

    @RequestLine("POST /test")
    @Headers("Content-Type: " + MediaType.APPLICATION_OCTET_STREAM_VALUE)
    Mono<ByteBuffer> test(ByteBuffer body);
}
