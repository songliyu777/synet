package com.synet.net.session;

public class EmptySession implements ISession {
    @Override
    public long getId() {
        return 0;
    }

    @Override
    public void send(byte[] data) {

    }
}
