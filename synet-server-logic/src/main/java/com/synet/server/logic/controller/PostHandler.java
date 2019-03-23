package com.synet.server.logic.controller;


import com.google.protobuf.AbstractMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.synet.message.IMessage;
import com.synet.protobuf.TestOuterClass;
import com.synet.protocol.ProtocolHeadDefine;
import com.synet.protocol.TcpNetProtocol;
import com.synet.server.logic.message.PBMessage;
import com.synet.server.logic.process.TestProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.function.Function;

@Component
public class PostHandler {

    @Autowired
    TestProcess process;

    /**
     * 处理流程类似转换，先从buffer转换成message,然后message进入处理流程，处理完之后转换成ServerResponse
     * buffer->message->process->message->protocol->response
     */
    Function<? super ByteBuffer, ? extends IMessage<AbstractMessage>> bufferToMessage = (buffer) -> {
        int serial = buffer.getInt(TcpNetProtocol.serial_index);
        short cmd = buffer.getShort(TcpNetProtocol.cmd_index);
        long session = buffer.getLong(TcpNetProtocol.session_index);
        buffer.position(TcpNetProtocol.protobuf_index);
        TestOuterClass.Test test = null;
        try {
            ByteBuffer pbuffer = buffer.slice();
            if (pbuffer.remaining() > 0) {
                test = TestOuterClass.Test.parseFrom(pbuffer);
            }
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
        return new PBMessage(serial, cmd, session, test);
    };

    Function<? super IMessage<AbstractMessage>, ? extends Mono<? extends IMessage<AbstractMessage>>> transformer = (message) -> {

        return process.process(message);
    };

    Function<? super IMessage<AbstractMessage>, ? extends Mono<ServerResponse>> messageToResponse = (message) -> {
        TcpNetProtocol protocol = TcpNetProtocol.create(ProtocolHeadDefine.ENCRYPT_PROTOBUF_HEAD,
                ProtocolHeadDefine.VERSION,
                message.getSerial(),
                message.getCmd(),
                message.getSession(),
                message.getMessage() == null ? null : message.getMessage().toByteArray());
        protocol.getByteBuffer().position(0);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(BodyInserters.fromObject(protocol.getByteBuffer()));
    };


    public Mono<ServerResponse> test(ServerRequest req) {
        return req.body(BodyExtractors.toMono(ByteBuffer.class))
                .map(bufferToMessage)
                .flatMap(transformer)
                .flatMap(messageToResponse);
    }

}
