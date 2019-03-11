package com.synet.protocol;

import io.netty.buffer.ByteBuf;

public class ProtocolBody {

    ByteBuf byteBuf = null;

    public ProtocolBody(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    /**
     * 序列号
     */
    public int getSerial() {
        return byteBuf.getInt(8);
    }

    /**
     * 命令
     */
    public short getCmd() {
        return byteBuf.getShort(12);
    }

    /**
     * protobuf 数据
     */
    public void GetProtobuf(ByteBuf protobuf) {
        byteBuf.getBytes(14, protobuf);
    }

    public void SetProtobuf(byte[] protobuf) {
        byteBuf.setBytes(14, protobuf);
    }
}
