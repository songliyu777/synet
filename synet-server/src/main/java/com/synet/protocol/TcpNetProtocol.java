package com.synet.protocol;

import io.netty.buffer.ByteBuf;

public class TcpNetProtocol implements IProtocol {

    ProtocolBody body;
    ProtocolHead head;

    public static TcpNetProtocol Parse(ByteBuf buf) throws Exception {

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

    private TcpNetProtocol() {
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

    public void decode(byte[] bytes) throws ProtocolPaseExcetion {
        throw new ProtocolPaseExcetion("tcp decode");
    }
}
