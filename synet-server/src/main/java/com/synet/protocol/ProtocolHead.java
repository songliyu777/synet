package com.synet.protocol;

import io.netty.buffer.ByteBuf;

public class ProtocolHead {

    ByteBuf byteBuf;

//    private byte head;
//    private byte version;
//    private int length;
//    private short checksum;
//    private int serial;
//    private short cmd;


    public ProtocolHead(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    /**
     * 头长度
     */
    public int getSize() {
        return 14;
    }

    /**
     * 识别头
     */
    public byte getHead() {
        return byteBuf.getByte(0);
    }

    public void setHead(byte head) {
        byteBuf.setByte(0, head);
    }

    /**
     * 版本号
     */
    public byte getVersion() {
        return byteBuf.getByte(1);
    }

    public void setVersion(byte version) {
        byteBuf.setByte(1, version);
    }

    /**
     * 长度
     */
    public int getLength() {
        return byteBuf.getInt(4);
    }

    public void setLength(int length) {
        byteBuf.setInt(2, length);
    }

    /**
     * 校验和
     */
    public short getChecksum() {
        return byteBuf.getShort(6);
    }

    public void setChecksum(short checksum) {
        byteBuf.setShort(6, checksum);
    }

    /**
     * 序列号
     */
    public int getSerial() {
        return byteBuf.getInt(8);
    }

    public void setSerial(int serial) {
        byteBuf.setInt(8, serial);
    }

    /**
     * 命令
     */
    public short getCmd() {
        return byteBuf.getShort(12);
    }

    public void setCmd(short cmd) {
        byteBuf.setShort(12, cmd);
    }

}
