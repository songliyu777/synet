package com.synet.net.service;

import java.nio.ByteBuffer;

public interface NetService {
    void send(long id, ByteBuffer buffer);
}
