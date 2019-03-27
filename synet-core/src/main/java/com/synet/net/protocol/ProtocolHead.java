package com.synet.net.protocol;

import java.nio.ByteBuffer;

public class ProtocolHead {

    public static int headSize = NetProtocol.protobuf_index;

    ByteBuffer byteBuffer;

    public ProtocolHead(ByteBuffer byteBuffer) {
        this.byteBuffer = byteBuffer;
    }

    /**
     * 识别头
     */
    public byte getHead() {
        return byteBuffer.get(NetProtocol.head_index);
    }

    public void setHead(byte head) {
        byteBuffer.put(NetProtocol.head_index, head);
    }

    /**
     * 版本号
     */
    public byte getVersion() {
        return byteBuffer.get(NetProtocol.version_index);
    }

    public void setVersion(byte version) {
        byteBuffer.put(NetProtocol.version_index, version);
    }

    /**
     * 长度
     */
    public int getLength() {
        return byteBuffer.getInt(NetProtocol.length_index);
    }

    public void setLength(int length) {
        byteBuffer.putInt(NetProtocol.length_index, length);
    }

    /**
     * 校验和
     */
    public short getChecksum() {
        return byteBuffer.getShort(NetProtocol.checksum_index);
    }

    public void setChecksum(short checksum) {
        byteBuffer.putShort(NetProtocol.checksum_index, checksum);
    }

    /**
     * 序列号
     */
    public int getSerial() {
        return byteBuffer.getInt(NetProtocol.serial_index);
    }

    public void setSerial(int serial) {
        byteBuffer.putInt(NetProtocol.serial_index, serial);
    }

    /**
     * 命令
     */
    public short getCmd() {
        return byteBuffer.getShort(NetProtocol.cmd_index);
    }

    public void setCmd(short cmd) {
        byteBuffer.putShort(NetProtocol.cmd_index, cmd);
    }

    /**
     * Session
     */
    public long getSession() {
        return byteBuffer.getLong(NetProtocol.session_index);
    }

    public void setSession(long session) {
        byteBuffer.putLong(NetProtocol.session_index, session);
    }

}
