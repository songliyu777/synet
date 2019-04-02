package com.synet.net.protobuf.mapping;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtoHeader {
    private int serial;
    private short cmd;
    private long session;
    private String host;
}
