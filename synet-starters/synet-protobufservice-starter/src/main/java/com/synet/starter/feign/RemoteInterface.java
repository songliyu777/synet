package com.synet.starter.feign;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

public interface RemoteInterface {
    /**
     * Gateway remote query
     * @param body
     * @param remote 127.0.0.1 or 127.0.0.1:9000
     * @return
     */
    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,  headers = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    Mono<ByteBuffer> test(ByteBuffer body, @RequestParam("remote") String remote);
}
