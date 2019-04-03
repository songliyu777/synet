package com.synet.net.net;

import java.nio.ByteBuffer;

public interface NetServive {
    void send(long id, ByteBuffer buffer);
}
