package com.synet.session;

import reactor.core.publisher.Flux;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;

public class TcpSession implements ISession {

    long id;
    Connection connection;

    public TcpSession(long id, Connection connection) throws RuntimeException {
        this.connection = connection;
        if (!connection.channel().isActive()) {
            throw new SessionException("connection.channel().isInActive()");
        }
        connection.channel().attr(SessionManager.channel_session_id).set(id);
    }

    @Override
    public long GetId() {
        return id;
    }

    @Override
    public void Send(byte[] data) {
        ByteBufFlux f = ByteBufFlux.fromInbound(Flux.just(data));
        connection.outbound().send(f).then().subscribe();
    }
}
