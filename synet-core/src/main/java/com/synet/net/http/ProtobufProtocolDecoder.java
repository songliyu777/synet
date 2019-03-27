package com.synet.net.http;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;
import com.synet.net.protocol.NetProtocol;
import org.springframework.core.MethodParameter;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

public class ProtobufProtocolDecoder implements ProtocolDecoder<Message> {

    private static final ConcurrentMap<Class<?>, Method> methodCache = new ConcurrentReferenceHashMap<>();

    private final ExtensionRegistry extensionRegistry;

    public ProtobufProtocolDecoder() {
        this.extensionRegistry = ExtensionRegistry.newInstance();
    }

    @Override
    public Message decode(ByteBuffer byteBuffer, MethodParameter bodyParameter) {
        try {
            if (Objects.isNull(bodyParameter)) {
                return null;
            }
            Message.Builder builder = getMessageBuilder(bodyParameter.getParameterType());
            byteBuffer.position(NetProtocol.protobuf_index);
            builder.mergeFrom(CodedInputStream.newInstance(byteBuffer), this.extensionRegistry);
            return builder.build();
        } catch (IOException ex) {
            throw new DecodingException("I/O error while parsing input stream", ex);
        } catch (Exception ex) {
            throw new DecodingException("Could not read Protobuf message: " + ex.getMessage(), ex);
        } finally {
            byteBuffer.position(0);
        }
    }

    /**
     * Create a new {@code Message.Builder} instance for the given class.
     * <p>This method uses a ConcurrentHashMap for caching method lookups.
     */
    private static Message.Builder getMessageBuilder(Class<?> clazz) throws Exception {
        Method method = methodCache.get(clazz);
        if (method == null) {
            method = clazz.getMethod("newBuilder");
            methodCache.put(clazz, method);
        }
        return (Message.Builder) method.invoke(clazz);
    }
}
