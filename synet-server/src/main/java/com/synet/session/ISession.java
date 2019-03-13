package com.synet.session;

public interface ISession {
    long getId();

    void send(byte[] data);
}
