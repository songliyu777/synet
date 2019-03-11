package com.synet.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class TcpNetProtocol implements IProtocol {

    protected ProtocolHead head;
    protected ProtocolBody body;
    protected ByteBuf protocolBuf;

    public static TcpNetProtocol Parse(ByteBuf recvbuf) throws RuntimeException {

        ByteBuf protocolBuf = Unpooled.buffer(recvbuf.readableBytes());
        recvbuf.readBytes(protocolBuf);
        TcpNetProtocol protocol = new TcpNetProtocol(protocolBuf);
        //解密
        //decode(bytes);

        return protocol;
    }

    public static TcpNetProtocol create(byte head, byte version, int serial, short command, byte[] protobuf) {

        ByteBuf protocolBuf = Unpooled.buffer(14 + protobuf.length);

        TcpNetProtocol protocol = new TcpNetProtocol(protocolBuf);
        //设置封包头
        protocol.head.setHead(head);
        protocol.head.setVersion(version);
        protocol.head.setLength(protobuf.length);
        protocol.head.setSerial(serial);
        protocol.head.setCmd(command);

        //加密
        //decode(bytes);
        protocol.head.setChecksum((short) 0);
        protocol.body.SetProtobuf(protobuf);

        return protocol;
    }

    public byte[] toBodyArray() throws ProtocolExcetion {
        ByteBuf b = protocolBuf.skipBytes(8);
        if (!b.hasArray()) {
            throw new ProtocolExcetion("toBodyArray no array");
        }
        return protocolBuf.skipBytes(8).array();
    }

    public byte[] toArray() {
        return protocolBuf.array();
    }

    public void release() {
        if (protocolBuf != null) {
            protocolBuf.release();
        }
    }

    protected TcpNetProtocol(ByteBuf byteBuf) {
        body = new ProtocolBody(byteBuf);
        head = new ProtocolHead(byteBuf);
        protocolBuf = byteBuf;
    }

    @Override
    public ProtocolHead getHead() {
        return head;
    }

    @Override
    public ProtocolBody getBody() {
        return body;
    }

    public short decode(byte[] bytes) throws ProtocolExcetion {
        throw new ProtocolExcetion("tcp decode");
    }

    public short encode(byte[] bytes) throws ProtocolExcetion {
        throw new ProtocolExcetion("tcp encode");
    }
}
