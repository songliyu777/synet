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
import com.synet.net.protocol.ProtocolHeadDefine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
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

    private int serial = 0;

    private long session = 0;

    private short endOfCommand = 0;

    public MessageHandler(ProtobufMappingHandler protobufMappingHandler) {
        this.protobufMappingHandler = protobufMappingHandler;
        try {
            protobufMappingHandler.initProtobufMethods(this);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        decoder = new ProtobufProtocolDecoder();
        encoder = new ProtobufProtocolEncoder();
    }

    public void setEndOfCommand(short endOfCommand) {
        this.endOfCommand = endOfCommand;
    }

    public short getEndOfCommand() {
        return this.endOfCommand;
    }

    public String handle(ByteBuffer byteBuffer) {

        NetProtocol protocol = NetProtocol.wrap(byteBuffer);

        //找到调用方法
        ProtobufMethod protobufMethod = protobufMappingHandler.getProtobufMethod(protocol.getHead().getCmd());
        if (Objects.isNull(protobufMethod)) {
            throw new RuntimeException("no method");
        }

        log.info("handle command:" + protocol.getHead().getCmd());

        MethodParameter bodyParameter = null;
        MethodParameter[] methodParameters = protobufMethod.getMethodParameters();
        if (Objects.isNull(methodParameters)) {
            throw new RuntimeException("no method parameters");
        }

        for (MethodParameter methodParameter : methodParameters) {
            if (methodParameter.hasParameterAnnotation(Body.class)) {
                bodyParameter = methodParameter;
            }
        }

        Message message = decoder.decode(byteBuffer, bodyParameter);
        Object[] args = getMethodArgumentValues(protobufMethod, protocol.getProtoHeader(), message);
        Object value = null;

        try {
            value = protobufMethod.getMethod().invoke(protobufMethod.getBean(), args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if (Objects.isNull(value)) {
            throw new RuntimeException("no return");
        }

        log.info("hander return:" + value.toString());

        return value.toString();
    }

    public void send(OutputStream os, String cmdLine) throws IOException {
        this.os = os;
        log.info(cmdLine);
        String[] commands = cmdLine.split(" ");
        int length = commands.length;
        if (length == 0) {
            throw new RuntimeException("command length is zero");
        }
        CommandMethod commandMethod = protobufMappingHandler.getCommandMethod(commands[0]);
        if (commandMethod == null) {
            throw new RuntimeException("no command method");
        }
        Object[] args = getMethodArgumentValues(commandMethod, commands);
        Object value = null;
        try {
            value = commandMethod.getMethod().invoke(commandMethod.getBean(), args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        if (Objects.isNull(value)) {
            throw new RuntimeException("no return");
        }

        if (value instanceof Message) {
            serial++;
            NetProtocol protocol = NetProtocol.create(ProtocolHeadDefine.ENCRYPT_PROTOBUF_HEAD, ProtocolHeadDefine.VERSION, serial, commandMethod.getCmd(), session, ((Message) value).toByteArray());
            os.write(protocol.toArray());
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
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
            String strParam = commands[i + 1];
            Object objParam = CastParam(parameter.getParameterType(), strParam);
            if (Objects.isNull(objParam)) {
                log.error("no correct param:" + commandMethod.getMethod().getName());
                return EMPTY_ARGS;
            }
            args.add(objParam);
        }
        return args.toArray();
    }

    Object CastParam(Class<?> type, String param) {
        if (type == String.class) {
            return param;
        }
        if (type == Integer.class) {
            return Integer.valueOf(param);
        }
        log.error("no support type:" + type);
        return null;
    }
}
