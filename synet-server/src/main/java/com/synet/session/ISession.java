package com.synet.session;

public interface ISession {
    long GetId();

    void Send(byte[] data);
}
