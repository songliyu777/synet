package com.synet.starter.protobufservice;

import com.synet.net.http.ProtobufProtocolDecoder;
import com.synet.net.http.ProtobufProtocolEncoder;
import com.synet.net.protobuf.handler.DefaultProtobufHandler;
import com.synet.net.protobuf.mapping.ProtobufMappingHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProtobufServiceConfiguration {

    //for SynetRemoteInterface invoke
    public static ProtobufProtocolEncoder encoder;

    @Bean
    public RouterFunction<ServerResponse> monoRouterFunction(DefaultProtobufHandler protobufHandler) {
        return route(POST("/pb/protocol").
                and(accept(APPLICATION_OCTET_STREAM)), protobufHandler::handle);
    }

    @Bean
    public DefaultProtobufHandler defaultProtobufHandler(ProtobufMappingHandlerMapping protobufMappingHandlerMapping,
                                                         ProtobufProtocolEncoder protobufProtocolEncoder,
                                                         ProtobufProtocolDecoder protobufProtocolDecoder) {
        return new DefaultProtobufHandler(protobufMappingHandlerMapping, protobufProtocolDecoder, protobufProtocolEncoder);
    }

    @Bean
    public ProtobufMappingHandlerMapping protobufMappingHandlerMapping() {
        return new ProtobufMappingHandlerMapping();
    }

    @Bean
    public ProtobufProtocolEncoder protobufProtocolEncoder() {
        encoder = new ProtobufProtocolEncoder();
        return encoder;
    }

    @Bean
    public ProtobufProtocolDecoder protobufProtocolDecoder() {
        return new ProtobufProtocolDecoder();
    }

}
