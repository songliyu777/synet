package com.synet.net.http;

import com.google.protobuf.Message;
import com.synet.net.protobuf.mapping.ProtoHeader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

import java.nio.ByteBuffer;
import java.util.Objects;


public class ProtobufProtocolEncoder implements ProtocolEncoder<Message>{

    @Override
    public DataBuffer encode(ProtoHeader header, Message body) {

        int bodyLength = 0;
        byte[] bytes = null;
        if (Objects.nonNull(body)) {
            bytes = body.toByteArray();
            bodyLength = bytes.length;
        }

        //设置部分header值
        header.setLength(bodyLength);
        header.setChecksum((short) 13);

        ByteBuffer byteBuffer = ByteBuffer.allocate(CodecUtils.protobuf_index + bodyLength);
        DefaultDataBuffer dataBuffer = new DefaultDataBufferFactory().wrap(byteBuffer);
        this.encode(dataBuffer, header);
        if (bodyLength != 0) {
            this.encode(dataBuffer, bytes);
        }

        return dataBuffer;
    }

    @Override
    public DataBuffer encode(DataBuffer dest, ProtoHeader header) {
        ByteBuffer byteBuffer = dest.asByteBuffer(0, CodecUtils.protobuf_index);
        byteBuffer.put(CodecUtils.head_index, header.getHead());
        byteBuffer.put(CodecUtils.version_index, header.getVersion());
        byteBuffer.putInt(CodecUtils.length_index, header.getLength());
        byteBuffer.putInt(CodecUtils.serial_index, header.getSerial());
        byteBuffer.putShort(CodecUtils.cmd_index, header.getCmd());
        byteBuffer.putLong(CodecUtils.session_index, header.getSession());
        byteBuffer.putShort(CodecUtils.checksum_index, header.getChecksum());
        return dest;
    }

    @Override
    public DataBuffer encode(DataBuffer dest, byte[] body) {
        ByteBuffer byteBuffer = dest.asByteBuffer(CodecUtils.protobuf_index, body.length);
        byteBuffer.put(body);
        return dest;
    }
}
