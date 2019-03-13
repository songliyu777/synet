package com.synet.protocol;

import io.netty.buffer.ByteBuf;

public class ProtocolBody {

    ByteBuf byteBuf = null;

    public ProtocolBody(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    /**
     * protobuf 数据
     */
    public void getProtobuf(ByteBuf protobuf) {
        byteBuf.getBytes(TcpNetProtocol.protobuf_index, protobuf);
    }

    public void setProtobuf(byte[] protobuf) {
        byteBuf.setBytes(TcpNetProtocol.protobuf_index, protobuf);
    }


}
