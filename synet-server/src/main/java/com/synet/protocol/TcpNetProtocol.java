package com.synet.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteBuffer;

public class TcpNetProtocol implements IProtocol {

    public static int head_index = 0;
    public static int version_index = 1;
    public static int length_index = 2;
    public static int checksum_index = 6;
    public static int serial_index = 8;
    public static int cmd_index = 12;
    public static int session_index = 14;
    public static int protobuf_index = 22;

    protected ProtocolHead head;
    protected ProtocolBody body;
    protected ByteBuf protocolBuf;

    public static TcpNetProtocol parse(ByteBuf recvbuf) throws RuntimeException {

        ByteBuf protocolBuf = Unpooled.buffer(recvbuf.readableBytes());
        recvbuf.readBytes(protocolBuf);
        TcpNetProtocol protocol = new TcpNetProtocol(protocolBuf);
        //解密
        //decode(bytes);

        return protocol;
    }

    public static TcpNetProtocol create(ByteBuffer buffer) throws RuntimeException {

        ByteBuf protocolBuf = Unpooled.buffer(buffer.remaining());
        buffer.get(protocolBuf.array());
        TcpNetProtocol protocol = new TcpNetProtocol(protocolBuf);
        //加密
        //encode(bytes);

        return protocol;
    }

    public static TcpNetProtocol create(byte head, byte version, int serial, short command, long session, byte[] protobuf) {

        ByteBuf protocolBuf = Unpooled.buffer(ProtocolHead.headSize + protobuf.length);

        TcpNetProtocol protocol = new TcpNetProtocol(protocolBuf);
        //设置封包头
        protocol.head.setHead(head);
        protocol.head.setVersion(version);
        protocol.head.setLength(protobuf.length);
        protocol.head.setSerial(serial);
        protocol.head.setCmd(command);
        protocol.head.setSession(session);

        //加密
        //decode(bytes);
        protocol.head.setChecksum((short) 0);
        protocol.body.setProtobuf(protobuf);

        return protocol;
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

    public int getSize(){
        return protocolBuf.readableBytes();
    }

    public short decode(byte[] bytes) throws ProtocolExcetion {
        throw new ProtocolExcetion("tcp decode");
    }

    public short encode(byte[] bytes) throws ProtocolExcetion {
        throw new ProtocolExcetion("tcp encode");
    }
}
