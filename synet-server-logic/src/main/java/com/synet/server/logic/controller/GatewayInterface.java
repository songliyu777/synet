package com.synet.server.logic.controller;

import com.synet.starter.feign.SynetRemoteInterface;
import reactivefeign.spring.config.ReactiveFeignClient;


//public interface GatewayInterfaceSynet {
//
//    @RequestLine("POST /test")
//    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE,  headers = MediaType.APPLICATION_OCTET_STREAM_VALUE)
//    Mono<Void> test(ByteBuffer body);
//}

@ReactiveFeignClient(name = "server-gateway")
public interface GatewayInterface extends SynetRemoteInterface {

}

