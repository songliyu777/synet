package com.synet.protocol;

import io.netty.buffer.ByteBuf;

import java.util.Objects;

public class ProtocolHead {

    public static int headSize = TcpNetProtocol.protobuf_index;

    ByteBuf byteBuf;

    public ProtocolHead(ByteBuf byteBuf) {
        Objects.requireNonNull(byteBuf);
        this.byteBuf = byteBuf;
    }

    /**
     * 识别头
     */
    public byte getHead() {
        return byteBuf.getByte(TcpNetProtocol.head_index);
    }

    public void setHead(byte head) {
        byteBuf.setByte(TcpNetProtocol.head_index, head);
    }

    /**
     * 版本号
     */
    public byte getVersion() {
        return byteBuf.getByte(TcpNetProtocol.version_index);
    }

    public void setVersion(byte version) {
        byteBuf.setByte(TcpNetProtocol.version_index, version);
    }

    /**
     * 长度
     */
    public int getLength() {
        return byteBuf.getInt(TcpNetProtocol.length_index);
    }

    public void setLength(int length) {
        byteBuf.setInt(TcpNetProtocol.length_index, length);
    }

    /**
     * 校验和
     */
    public short getChecksum() {
        return byteBuf.getShort(TcpNetProtocol.checksum_index);
    }

    public void setChecksum(short checksum) {
        byteBuf.setShort(TcpNetProtocol.checksum_index, checksum);
    }

    /**
     * 序列号
     */
    public int getSerial() {
        return byteBuf.getInt(TcpNetProtocol.serial_index);
    }

    public void setSerial(int serial) {
        byteBuf.setInt(TcpNetProtocol.serial_index, serial);
    }

    /**
     * 命令
     */
    public short getCmd() {
        return byteBuf.getShort(TcpNetProtocol.cmd_index);
    }

    public void setCmd(short cmd) {
        byteBuf.setShort(TcpNetProtocol.cmd_index, cmd);
    }

    /**
     * Session
     */
    public long getSession() {
        return byteBuf.getLong(TcpNetProtocol.session_index);
    }

    public void setSession(long session) {
        if(byteBuf == null){
            System.err.println("==>byteBuf == null");
        }
        byteBuf.setLong(TcpNetProtocol.session_index, session);
    }

}
