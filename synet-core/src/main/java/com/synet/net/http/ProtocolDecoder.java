package com.synet.net.http;

import com.synet.net.protobuf.mapping.ProtoHeader;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.buffer.DataBuffer;

import java.nio.ByteBuffer;

/**
 * 协议解析器
 *
 * @param <T>
 */
public interface ProtocolDecoder<T> {


    T decode(ByteBuffer byteBuffer, MethodParameter bodyParameter);

}
