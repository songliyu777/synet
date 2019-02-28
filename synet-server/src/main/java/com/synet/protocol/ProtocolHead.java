package com.synet.protocol;

public class ProtocolHead {
    private byte head;
    private byte version;
    private int length;
    private int serial;
    private short checksum;
    private short cmd;

    /**
     * 识别头
     */
    public byte getHead() {
        return head;
    }

    public void setHead(byte head) {
        this.head = head;
    }

    /**
     * 版本号
     */
    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    /**
     * 长度
     */
    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    /**
     * 序列号
     */
    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    /**
     * 命令
     */
    public short getCmd() {
        return cmd;
    }

    public void setCmd(short cmd) {
        this.cmd = cmd;
    }

    /**
     * 校验和
     */
    public short getChecksum() {
        return checksum;
    }

    public void setChecksum(short checksum) {
        this.checksum = checksum;
    }
}
