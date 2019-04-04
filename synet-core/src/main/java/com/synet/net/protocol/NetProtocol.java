package com.synet.net.protocol;

import com.synet.net.protobuf.mapping.ProtoHeader;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;

public class NetProtocol implements IProtocol {

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
    protected ByteBuffer protocolBuffer;

    public static NetProtocol parse(ByteBuf recvBuf) throws RuntimeException {
        ByteBuffer protocolBuffer = ByteBuffer.allocate(recvBuf.readableBytes());
        recvBuf.readBytes(protocolBuffer.array());
        NetProtocol protocol = new NetProtocol(protocolBuffer);
        //解密
        //decode(bytes);

        return protocol;
    }

    public static NetProtocol create(ByteBuffer buffer) throws RuntimeException {
        NetProtocol protocol = new NetProtocol(buffer);
        //加密
        //encode(bytes);

        return protocol;
    }

    public static NetProtocol wrap(ByteBuffer buffer){
        NetProtocol protocol = new NetProtocol(buffer);
        return protocol;
    }

    public static NetProtocol create(byte head, byte version, int serial, short command, long session, byte[] protobuf) {
        int protobuf_length = protobuf == null ? 0 : protobuf.length;
        ByteBuffer protocolBuffer = ByteBuffer.allocate(ProtocolHead.headSize + protobuf_length);

        NetProtocol protocol = new NetProtocol(protocolBuffer);
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
        if (protobuf_length > 0) {
            protocol.body.setProtobuf(protobuf);
        }
        protocolBuffer.clear();
        return protocol;
    }

    public byte[] toArray() {
        return protocolBuffer.array();
    }


    protected NetProtocol(ByteBuffer byteBuffer) {
        body = new ProtocolBody(byteBuffer);
        head = new ProtocolHead(byteBuffer);
        protocolBuffer = byteBuffer;
    }

    @Override
    public ProtocolHead getHead() {
        return head;
    }

    public ProtoHeader getProtoHeader(String remoteAddress){
        return  ProtoHeader.builder()
                .cmd(getHead().getCmd())
                .serial(getHead().getSerial())
                .session(getHead().getSession())
                .remoteAddress(remoteAddress).build();
    }

    @Override
    public ProtocolBody getBody() {
        return body;
    }

    public ByteBuffer getByteBuffer() {
        return protocolBuffer;
    }

    public int getSize() {
        return protocolBuffer.remaining();
    }

    public short decode(byte[] bytes) throws ProtocolExcetion {
        throw new ProtocolExcetion("gametcp decode");
    }

    public short encode(byte[] bytes) throws ProtocolExcetion {
        throw new ProtocolExcetion("gametcp encode");
    }
}
