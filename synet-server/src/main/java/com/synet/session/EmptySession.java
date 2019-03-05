package com.synet.session;

public class EmptySession implements ISession {
    @Override
    public long GetId() {
        return 0;
    }

    @Override
    public void Send(byte[] data) {

    }
}
