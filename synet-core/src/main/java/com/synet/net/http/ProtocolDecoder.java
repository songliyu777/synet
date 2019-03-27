package com.synet.net.http;

import com.synet.net.protobuf.mapping.ProtoHeader;
import org.springframework.core.io.buffer.DataBuffer;

import java.nio.ByteBuffer;

/**
 * 协议解析器
 *
 * @param <T>
 */
public interface ProtocolDecoder<T> {

    /**
     * 获取协议头
     *
     * @param dataBuffer 数据buffer
     * @return 协议头
     */
    default ProtoHeader decode(DataBuffer dataBuffer) {
        ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
        int serial = byteBuffer.getInt(CodecUtils.serial_index);
        short cmd = byteBuffer.getShort(CodecUtils.cmd_index);
        long session = byteBuffer.getLong(CodecUtils.session_index);
        return ProtoHeader.builder().serial(serial).cmd(cmd).session(session).build();
    }


    T decode(DataBuffer dataBuffer, Class<?> elementType);

}
