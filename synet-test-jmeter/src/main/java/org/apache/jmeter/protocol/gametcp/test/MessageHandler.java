package org.apache.jmeter.protocol.gametcp.test;

import com.google.protobuf.Message;
import com.synet.net.http.ProtobufProtocolDecoder;
import com.synet.net.http.ProtobufProtocolEncoder;
import com.synet.net.http.ProtocolDecoder;
import com.synet.net.http.ProtocolEncoder;
import com.synet.net.protobuf.mapping.Body;
import com.synet.net.protobuf.mapping.Header;
import com.synet.net.protobuf.mapping.ProtoHeader;
import com.synet.net.protobuf.mapping.ProtobufMethod;
import com.synet.net.protocol.NetProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class MessageHandler {

    private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);

    private static final Object[] EMPTY_ARGS = new Object[0];

    private static final Object NO_ARG_VALUE = new Object();

    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    ProtobufMappingHandler protobufMappingHandler;

    private ProtocolDecoder<Message> decoder;

    private ProtocolEncoder<Message> encoder;

    private OutputStream os;

    public MessageHandler() {
        protobufMappingHandler = new ProtobufMappingHandler();
        protobufMappingHandler.initProtobufMethods();
        decoder = new ProtobufProtocolDecoder();
        encoder = new ProtobufProtocolEncoder();
    }

    public void handle(ByteBuffer byteBuffer) {

        NetProtocol protocol = NetProtocol.wrap(byteBuffer);

        //找到调用方法
        ProtobufMethod protobufMethod = protobufMappingHandler.getProtobufMethod(protocol.getHead().getCmd());
        if (Objects.isNull(protobufMethod)) {
            log.error("no method");
        }

        MethodParameter bodyParameter = null;
        MethodParameter[] methodParameters = protobufMethod.getMethodParameters();
        for (MethodParameter methodParameter : methodParameters) {
            if (methodParameter.hasParameterAnnotation(Body.class)) {
                bodyParameter = methodParameter;
            }
        }

        Message message = decoder.decode(byteBuffer, bodyParameter);
        Object[] args = getMethodArgumentValues(protobufMethod, protocol.getProtoHeader(""), message);
        Object value = null;

        try {
            value = protobufMethod.getMethod().invoke(protobufMethod.getBean(), args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if (Objects.isNull(value)) {
            log.error("no return");
        }
    }

    public void send(OutputStream os, String cmdLine) {
        this.os = os;
        log.info(cmdLine);
        String[] commands = cmdLine.split(" ");
        int length = commands.length;
        if (length == 0) {
            log.error("command length is zero");
            return;
        }
        CommandMethod commandMethod = protobufMappingHandler.getCommandMethod(commands[0]);
        if (commandMethod == null) {
            log.error("no method");
            return;
        }
        Object[] args =
        MethodParameter[] methodParameters = commandMethod.getMethodParameters();
        for (int i=0;i<methodParameters.length;i++)
        {
            Object p = methodParameters[i].getParameterType().cast(commands[i+1]);
        }
        for (MethodParameter methodParameter : methodParameters) {
            Type t = methodParameter.getGenericParameterType();

        }
    }

    private Object[] getMethodArgumentValues(ProtobufMethod protobufMethod, ProtoHeader header, Message message) {
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
                    args.add(header);
                } else if (annotation instanceof Body) {
                    args.add(message);
                }
            });
        }
        return args.toArray();
    }

    private Object[] getMethodArgumentValues(CommandMethod commandMethod, String[] commands) {
        MethodParameter[] parameters = commandMethod.getMethodParameters();
        if (ObjectUtils.isEmpty(parameters)) {
            return EMPTY_ARGS;
        }
        List<Object> args = new ArrayList<>(parameters.length);
        for (MethodParameter parameter : parameters) {
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
            Annotation[] parameterAnnotations = parameter.getParameterAnnotations();
            Stream.of(parameterAnnotations).forEach(annotation -> {
                if (annotation instanceof Header) {
                    args.add(header);
                } else if (annotation instanceof Body) {
                    args.add(message);
                }
            });
        }
        return args.toArray();
    }
}
