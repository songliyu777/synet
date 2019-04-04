package com.synet.net.tcp;

import com.synet.net.session.ISession;
import com.synet.net.session.SessionException;
import com.synet.net.session.SessionManager;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.channel.AbortedException;

import java.io.IOException;

@Slf4j
public class TcpSession implements ISession {

    long id;
    Connection connection;

    public TcpSession(long id, Connection connection) throws RuntimeException {
        this.connection = connection;
        if (!connection.channel().isActive()) {
            throw new SessionException("connection.channel().isInActive()");
        }
        connection.channel().attr(SessionManager.channel_session_id).set(id);
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void send(byte[] data) {
        if (!connection.isDisposed()) {
            ByteBufFlux f = ByteBufFlux.fromInbound(Flux.just(data));
            connection.outbound().send(f).then().doOnError((e) -> {
                if (e instanceof AbortedException || e instanceof IOException) {
                    return;
                }
                e.printStackTrace();
            }).subscribe();
        }
    }
}
