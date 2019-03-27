package com.synet.net.http;

import com.google.protobuf.Message;
import com.synet.net.protobuf.mapping.ProtoHeader;
import com.synet.net.protocol.NetProtocol;
import com.synet.net.protocol.ProtocolHeadDefine;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

import java.nio.ByteBuffer;
import java.util.Objects;


public class ProtobufProtocolEncoder implements ProtocolEncoder<Message> {

    @Override
    public ByteBuffer encode(ProtoHeader header, Message body) {

        return NetProtocol.create(ProtocolHeadDefine.ENCRYPT_PROTOBUF_HEAD,
                ProtocolHeadDefine.VERSION,
                header.getSerial(),
                header.getCmd(),
                header.getSession(),
                Objects.isNull(body) ? null : body.toByteArray()).getByteBuffer();
    }
}
