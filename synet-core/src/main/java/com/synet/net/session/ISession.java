package com.synet.net.session;

public interface ISession {
    long getId();

    void send(byte[] data);
}
