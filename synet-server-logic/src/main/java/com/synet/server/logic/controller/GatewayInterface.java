package com.synet.server.logic.controller;

import reactivefeign.spring.config.ReactiveFeignClient;


//public interface GatewayInterface {
//
//    @RequestLine("POST /test")
//    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,  headers = MediaType.APPLICATION_OCTET_STREAM_VALUE)
//    Mono<Void> test(ByteBuffer body);
//}

@ReactiveFeignClient(name = "server-gateway")
public interface GatewayInterface {

    /**
     * Gateway remote query
     * @param body
     * @param remote 127.0.0.1 or 127.0.0.1:9000
     * @return
     */
//    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,  headers = MediaType.APPLICATION_OCTET_STREAM_VALUE)
//    Mono<ByteBuffer> test(ByteBuffer body, @RequestParam("remote") String remote);
}

