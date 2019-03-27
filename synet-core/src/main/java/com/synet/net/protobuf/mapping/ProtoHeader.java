package com.synet.net.protobuf.mapping;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtoHeader {
    private byte head;
    private byte version;
    private int length;
    private int serial;
    private short cmd;
    private long session;
    private short checksum;
}
