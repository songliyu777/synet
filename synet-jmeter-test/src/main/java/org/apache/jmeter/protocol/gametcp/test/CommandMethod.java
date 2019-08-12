package org.apache.jmeter.protocol.gametcp.test;

import com.synet.net.protobuf.mapping.ProtobufMethod;

import java.lang.reflect.Method;

public class CommandMethod extends ProtobufMethod {

    private short cmd;

    public CommandMethod(Object bean, Method method, short cmd) {
        super(bean, method);
        this.cmd = cmd;
    }

    public short getCmd() {
        return cmd;
    }
}
