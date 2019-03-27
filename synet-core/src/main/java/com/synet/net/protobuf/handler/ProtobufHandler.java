package com.synet.net.protobuf.handler;


import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

/**
 * @author konghang
 *
 * Protobuf
 */
public interface ProtobufHandler {

    /**
     * 处理
     *
     * @param request 请求
     * @return 响应
     */
    Mono<ServerResponse> handle(ServerRequest request);
}
