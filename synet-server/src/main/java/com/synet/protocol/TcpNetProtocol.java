package com.synet.protocol;

import io.netty.buffer.ByteBuf;

public class TcpNetProtocol implements IProtocol {

    ProtocolBody body;
    ProtocolHead head;

    public static TcpNetProtocol Parse(ByteBuf buf) throws RuntimeException {

        TcpNetProtocol protocol = new TcpNetProtocol();
        //设置封包头
        protocol.head.setHead(buf.readByte());
        protocol.head.setVersion(buf.readByte());
        protocol.head.setLength(buf.readInt());
        protocol.head.setSerial(buf.readInt());
        protocol.head.setChecksum(buf.readShort());
        protocol.head.setCmd(buf.readShort());
        //设置封包体
        int byteLength = buf.readableBytes();
        byte[] bytes = new byte[byteLength];

        //解密
        //decode(bytes);

        buf.getBytes(buf.readerIndex(), bytes);
        protocol.body.setBytes(bytes);

        return protocol;
    }

    public static TcpNetProtocol create(byte head, byte version, int length, int serial, short command, byte[] body, long session) {

        TcpNetProtocol protocol = new TcpNetProtocol();
        //设置封包头
        protocol.head.setHead(head);
        protocol.head.setVersion(version);
        protocol.head.setLength(length);
        protocol.head.setSerial(serial);
        protocol.head.setCmd(command);
        protocol.body.setBytes(body);

        //加密
        //decode(bytes);
        protocol.head.setChecksum((short) 0);

        return protocol;
    }


    protected TcpNetProtocol() {
        body = new ProtocolBody();
        head = new ProtocolHead();
    }

    @Override
    public ProtocolHead getHead() {
        return head;
    }

    @Override
    public ProtocolBody getBody() {
        return body;
    }

    public short decode(byte[] bytes) throws ProtocolPaseExcetion {
        throw new ProtocolPaseExcetion("tcp decode");
    }

    public short encode(byte[] bytes) throws ProtocolPaseExcetion {
        throw new ProtocolPaseExcetion("tcp encode");
    }
}
