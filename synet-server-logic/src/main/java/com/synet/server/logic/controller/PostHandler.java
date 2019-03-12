package com.synet.server.logic.controller;


import com.google.protobuf.InvalidProtocolBufferException;
import com.synet.protobuf.TestOuterClass;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@Component
public class PostHandler {

    public Mono<ServerResponse> test(ServerRequest req) {
        return req.body(BodyExtractors.toMono(ByteBuffer.class))
                .flatMap((buffer) -> {
                    int serial = buffer.getInt(8);
                    short cmd = buffer.getShort(12);
                    buffer.position(14);
                    try {
                        TestOuterClass.Test test = TestOuterClass.Test.parseFrom(buffer.slice());
                        System.err.println(test.getName() + ":" + test.getPassword());
                        return ServerResponse.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(BodyInserters.fromObject(ByteBuffer.wrap(test.toByteArray())));
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }

                    return ServerResponse.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).build();
                });
    }

}
