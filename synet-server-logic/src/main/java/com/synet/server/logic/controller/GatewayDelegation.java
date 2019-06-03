package com.synet.server.logic.controller;

import com.google.protobuf.Message;
import com.synet.net.http.ProtocolEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GatewayDelegation {

//    @Autowired
//    ProtocolEncoder<Message> encoder;

    @Autowired
    GatewayInterface remoteInterface;

//    @Override
//    public ProtocolEncoder<Message> getEncoder() {
//        return encoder;
//    }
//
//    @Override
//    public RemoteInterface getRemote() {
//        return null;
//    }
}
