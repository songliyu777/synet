package com.synet.net.protobuf.handler;

import com.google.protobuf.Message;
import com.synet.net.http.ProtocolDecoder;
import com.synet.net.http.ProtocolEncoder;
import com.synet.net.protobuf.mapping.*;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * 默认的处理器
 */
public class DefaultProtobufHandler implements ProtobufHandler{

    private ProtobufMappingHandlerMapping protobufMappingHandlerMapping;

    private ProtocolDecoder<Message> decoder;

    private ProtocolEncoder<Message> encoder;

    private static final Object[] EMPTY_ARGS = new Object[0];

    private static final Object NO_ARG_VALUE = new Object();

    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public DefaultProtobufHandler(ProtobufMappingHandlerMapping protobufMappingHandlerMapping,
                                  ProtocolDecoder<Message> decoder,
                                  ProtocolEncoder<Message> encoder) {
        this.protobufMappingHandlerMapping = protobufMappingHandlerMapping;
        this.decoder = decoder;
        this.encoder = encoder;
    }

    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        return request.body(BodyExtractors.toMono(DataBuffer.class)).map(dataBuffer -> {

            //解析协议头
            ProtoHeader header = decoder.decode(dataBuffer);

            //找到调用方法
            ProtobufMethod protobufMethod = protobufMappingHandlerMapping.getProtobufMethod(header.getCmd());
            if (Objects.isNull(protobufMethod)) {
                throw new RuntimeException("未找到命令");
            }

            MethodParameter bodyParameter = null;
            MethodParameter[] methodParameters = protobufMethod.getMethodParameters();
            for (MethodParameter methodParameter: methodParameters) {
                if (methodParameter.hasParameterAnnotation(Body.class)) {
                    bodyParameter = methodParameter;
                }
            }

            //找到协议body，protobuf对象类
//            Body bodyAnnotaiion = protobufMethod.getMethodAnnotation(Body.class);
            if (Objects.isNull(bodyParameter)) {
                throw new RuntimeException("未找到body注解");
            }

            //解析
            Message message = decoder.decode(dataBuffer, bodyParameter.getParameterType());

            //构建上下文
            ProtoRequest protoRequest = ProtoRequest.builder()
                    .protoHeader(header)
                    .message(message)
                    .build();
            ReactiveExchangeContext context = ReactiveExchangeContext.builder()
                    .request(protoRequest)
                    .build();

            return context;
        }).flatMap(context -> {
            ProtoRequest protoRequest = context.getRequest();
            ProtobufMethod protobufMethod = protobufMappingHandlerMapping.getProtobufMethod(protoRequest.getProtoHeader().getCmd());
            if (Objects.isNull(protobufMethod)) {
                throw new RuntimeException("未找到命令");
            }
            Object[] args = getMethodArgumentValues(protobufMethod, context);
            Object value = null;
            try {
                value = protobufMethod.getMethod().invoke(protobufMethod.getBean(), args);
            } catch (InvocationTargetException ex) {
                ex.printStackTrace();
            } catch (Throwable ex) {
                ex.printStackTrace();
                // Unlikely to ever get here, but it must be handled...
            }
            if (Objects.isNull(value)) {
                throw new RuntimeException("需要考虑的");
            }

            Mono<Message> mono = null;
            if (!(value instanceof Mono)) {
                mono = Mono.just((Message) value);
            }else {
                mono = (Mono<Message>)value;
            }

            return mono.map(message -> {
                ProtoHeader header = ProtoHeader.builder()
                        .cmd((short) 1)
                        .serial(1)
                        .session(context.getRequest().getProtoHeader().getSession())
                        .build();
                return encoder.encode(header, message);
            });
        }).flatMap(this::sendServerResponse);
    }

    private Mono<ServerResponse> sendServerResponse(DataBuffer dataBuffer) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(BodyInserters.fromObject(dataBuffer.asByteBuffer().array()));
    }

    private Object[] getMethodArgumentValues(ProtobufMethod protobufMethod, ReactiveExchangeContext context) {
        MethodParameter[] parameters = protobufMethod.getMethodParameters();
        if (ObjectUtils.isEmpty(parameters)) {
            return EMPTY_ARGS;
        }
        List<Object> args = new ArrayList<>(parameters.length);
        for (MethodParameter parameter : parameters) {
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
            Annotation[] parameterAnnotations = parameter.getParameterAnnotations();
            Stream.of(parameterAnnotations).forEach(annotation -> {
                if (annotation instanceof Header) {
                    args.add(context.getRequest().getProtoHeader());
                } else if (annotation instanceof Body) {
                    args.add(context.getRequest().getMessage());
                }
            });
        }
        return args.toArray();
    }

}
