package com.synet.net.protocol;

public interface IProtocol {

    ProtocolHead getHead();

    ProtocolBody getBody();
}
