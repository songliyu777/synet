package com.synet.protocol;

public interface IProtocol {

    ProtocolHead getHead();

    ProtocolBody getBody();
}
