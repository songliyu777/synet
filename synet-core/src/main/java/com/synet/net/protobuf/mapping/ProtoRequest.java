package com.synet.net.protobuf.mapping;

import com.google.protobuf.Message;
import com.synet.net.protocol.NetProtocol;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtoRequest {

    private ProtoHeader header;
    private Message message;

}
