package com.synet.protocol;

import java.nio.ByteBuffer;

public class ProtocolHead {

    public static int headSize = TcpNetProtocol.protobuf_index;

    ByteBuffer byteBuffer;

    public ProtocolHead(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    /**
     * 识别头
     */
    public byte getHead() {
        return byteBuffer.get(TcpNetProtocol.head_index);
    }

    public void setHead(byte head) {
        byteBuffer.put(TcpNetProtocol.head_index, head);
    }

    /**
     * 版本号
     */
    public byte getVersion() {
        return byteBuffer.get(TcpNetProtocol.version_index);
    }

    public void setVersion(byte version) {
        byteBuffer.put(TcpNetProtocol.version_index, version);
    }

    /**
     * 长度
     */
    public int getLength() {
        return byteBuffer.getInt(TcpNetProtocol.length_index);
    }

    public void setLength(int length) {
        byteBuffer.putInt(TcpNetProtocol.length_index, length);
    }

    /**
     * 校验和
     */
    public short getChecksum() {
        return byteBuffer.getShort(TcpNetProtocol.checksum_index);
    }

    public void setChecksum(short checksum) {
        byteBuffer.putShort(TcpNetProtocol.checksum_index, checksum);
    }

    /**
     * 序列号
     */
    public int getSerial() {
        return byteBuffer.getInt(TcpNetProtocol.serial_index);
    }

    public void setSerial(int serial) {
        byteBuffer.putInt(TcpNetProtocol.serial_index, serial);
    }

    /**
     * 命令
     */
    public short getCmd() {
        return byteBuffer.getShort(TcpNetProtocol.cmd_index);
    }

    public void setCmd(short cmd) {
        byteBuffer.putShort(TcpNetProtocol.cmd_index, cmd);
    }

    /**
     * Session
     */
    public long getSession() {
        return byteBuffer.getLong(TcpNetProtocol.session_index);
    }

    public void setSession(long session) {
        byteBuffer.putLong(TcpNetProtocol.session_index, session);
    }

}
