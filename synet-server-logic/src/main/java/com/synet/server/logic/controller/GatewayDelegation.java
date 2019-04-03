package com.synet.server.logic.controller;

import com.google.protobuf.Message;
import com.synet.net.http.ProtobufProtocolEncoder;
import com.synet.net.http.ProtocolEncoder;
import com.synet.net.protobuf.mapping.ProtoHeader;
import com.synet.starter.feign.RemoteDelegation;
import com.synet.starter.feign.RemoteInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@Service
public class GatewayDelegation implements RemoteDelegation {

    @Autowired
    ProtocolEncoder<Message> encoder;

    @Autowired
    RemoteInterface remoteInterface;

    @Override
    public ProtocolEncoder<Message> getEncoder() {
        return encoder;
    }

    @Override
    public RemoteInterface getRemote() {
        return remoteInterface;
    }
}
