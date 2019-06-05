package org.apache.jmeter.protocol.gametcp.test;

import com.google.common.collect.Maps;
import com.synet.net.protobuf.mapping.ProtobufController;
import com.synet.net.protobuf.mapping.ProtobufMapping;
import com.synet.net.protobuf.mapping.ProtobufMappingInfo;
import com.synet.net.protobuf.mapping.ProtobufMethod;
import org.apache.jmeter.protocol.gametcp.test.logic.GameLogicTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProtobufMappingHandler {

    private static final Logger log = LoggerFactory.getLogger(ProtobufMappingHandler.class);

    private static List<Class<?>> PROTOBUF_ANNOTATIONS = new ArrayList<Class<?>>();

    private Map<Short, ProtobufMethod> registerMap = Maps.newHashMap();

    private Map<String, CommandMethod> commandMap = Maps.newHashMap();

    static {
        PROTOBUF_ANNOTATIONS.add(GameLogicTest.class);
    }

    public void initProtobufMethods() {
        for (Class<?> c : PROTOBUF_ANNOTATIONS) {
            if (!c.isAnnotationPresent(ProtobufController.class)) {
                log.error(c + ": no anotation");
                continue;
            }
            Object bean = null;
            try {
                bean = c.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            Method[] methods = c.getMethods();
            for (Method m : methods) {
                if (m.isAnnotationPresent(ProtobufMapping.class)) {
                    ProtobufMapping annotation = m.getDeclaredAnnotation(ProtobufMapping.class);
                    registerProtobufMethod(bean, m, annotation.cmd());
                }
                if (m.isAnnotationPresent(ProtobufCmd.class)) {
                    ProtobufCmd annotation = m.getDeclaredAnnotation(ProtobufCmd.class);
                    commandProtobufMethod(bean, m, annotation.name(), annotation.cmd());
                }
            }
        }
    }

    private ProtobufMappingInfo getMappingForMethod(Method method) {
        ProtobufMapping annotation = method.getDeclaredAnnotation(ProtobufMapping.class);
        return new ProtobufMappingInfo(annotation.cmd());
    }

    private void registerProtobufMethod(Object handler, Method invocableMethod, short cmd) {
        ProtobufMethod protobufMethod = new ProtobufMethod(handler, invocableMethod);
        registerMap.put(cmd, protobufMethod);
    }

    private void commandProtobufMethod(Object handler, Method invocableMethod, String command, short cmd) {
        CommandMethod method = new CommandMethod(handler, invocableMethod, cmd);
        commandMap.put(command, method);
    }

    public ProtobufMethod getProtobufMethod(short cmd) {
        return registerMap.get(cmd);
    }

    public CommandMethod getCommandMethod(String cmd) {
        return commandMap.get(cmd);
    }
}
