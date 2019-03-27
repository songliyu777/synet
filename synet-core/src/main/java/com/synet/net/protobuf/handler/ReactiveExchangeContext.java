package com.synet.net.protobuf.handler;

import com.synet.net.protobuf.mapping.ProtoRequest;
import com.synet.net.protobuf.mapping.ProtoResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReactiveExchangeContext {

    private ProtoRequest request;

    private ProtoResponse response;

}
