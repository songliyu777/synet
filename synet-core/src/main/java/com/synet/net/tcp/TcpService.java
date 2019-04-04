package com.synet.net.tcp;

import com.synet.net.net.NetServive;
import com.synet.net.protocol.NetProtocol;
import com.synet.net.session.ISession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStoppedEvent;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.ByteBuffer;

@Slf4j
public class TcpService implements ApplicationListener, NetServive {

    TcpServiceHandler handler;

    TcpNetServer server;

    public TcpService(TcpServiceConfig config, TcpServiceHandler handler) throws Exception {
        this.handler = handler;
        server = new TcpNetServer(config.getHost(), config.getPort(), config.getReadIdleTime(), config.getWriteIdleTime());
        server.setProcessHandler(this::process);
        server.setErrorHandler(this::error);
        server.doOnConnection(this::connection);
        server.createServer();
    }

    public TcpNetServer GetServer() {
        return server;
    }

    void process(NetProtocol protocol) {
        Mono<ByteBuffer> buf = handler.invoke(protocol.getByteBuffer());
        buf.map((b) -> NetProtocol.create(b)).subscribe(t -> {
            server.send(t.getHead().getSession(), t.getByteBuffer());
        }, (e) -> log.error(e.toString()));
    }

    void connection(ISession session) {
        //明文发送sessionid用于加密
    }

    void error(Throwable e) {
        if (e instanceof IOException) {
            return;
        }
        e.printStackTrace();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.info(event.toString());
        if (event instanceof ContextStoppedEvent) {
            server.getServer().dispose();
        }
    }

    @Override
    public void send(long id, ByteBuffer buffer) {
        server.send(id, buffer);
    }
}
