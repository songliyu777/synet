package com.synet.starter.gatewayservice.service;

import feign.Headers;
import feign.RequestLine;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@Headers({ "Accept: application/octet-stream", "Content-Type: application/octet-stream" })
public interface LightningPbService {

    /**
     * 调用服务
     *
     * @param body
     * @return
     */
    @RequestLine("POST /pb/protocol")
    Mono<ByteBuffer> protocol(ByteBuffer body);
}
