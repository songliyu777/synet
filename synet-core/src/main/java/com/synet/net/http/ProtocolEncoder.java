package com.synet.net.http;

import com.synet.net.protobuf.mapping.ProtoHeader;
import org.springframework.core.io.buffer.DataBuffer;

import java.nio.ByteBuffer;

public interface ProtocolEncoder<T> {

    /**
     * 解析
     *
     * @param header 协议头
     * @param body 协议体
     * @return 数据buffer
     */
    ByteBuffer encode(ProtoHeader header, T body);
}
