package com.synet.net.http;

import com.synet.net.protobuf.mapping.ProtoHeader;
import org.springframework.core.io.buffer.DataBuffer;

public interface ProtocolEncoder<T> {

    /**
     * 解析
     *
     * @param header 协议头
     * @param body 协议体
     * @return 数据buffer
     */
    DataBuffer encode(ProtoHeader header, T body);

    /**
     * 编码协议头
     *
     * @param header 协议头
     * @param dest 目的buffer
     * @return 目的buffer
     */
    DataBuffer encode(DataBuffer dest, ProtoHeader header);

    /**
     * 编码协议体
     *
     * @param body 协议体
     * @param dest 目的buffer
     * @return 目的buffer
     */
    DataBuffer encode(DataBuffer dest, byte[] body);
}
