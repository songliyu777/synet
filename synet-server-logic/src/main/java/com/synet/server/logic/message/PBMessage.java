package com.synet.server.logic.message;

import com.google.protobuf.AbstractMessage;
import com.synet.net.message.IMessage;

public class PBMessage implements IMessage<AbstractMessage> {

    int serial;
    short cmd;
    long session;
    AbstractMessage message;

    public PBMessage(int serial, short cmd, long session, AbstractMessage message) {
        this.serial = serial;
        this.cmd = cmd;
        this.session = session;
        this.message = message;
    }

    @Override
    public int getSerial() {
        return serial;
    }

    @Override
    public short getCmd() {
        return cmd;
    }

    @Override
    public long getSession() {
        return session;
    }

    @Override
    public AbstractMessage getMessage() {
        return message;
    }
}
