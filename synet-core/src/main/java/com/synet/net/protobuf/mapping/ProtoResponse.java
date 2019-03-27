package com.synet.net.protobuf.mapping;

import com.google.protobuf.Message;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtoResponse {

    private ProtoHeader protoHeader;

    private Message message;
}
