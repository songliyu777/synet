package com.synet.net.tcp;

import com.synet.net.session.ISession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStoppedEvent;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@Slf4j
public class TcpService implements ApplicationListener {

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

    void process(TcpNetProtocol protocol) {
        Mono<ByteBuffer> buf = handler.invoke(protocol.getByteBuffer());
        buf.map((b) -> TcpNetProtocol.create(b)).subscribe(t -> {
            server.send(t.getHead().getSession(), t.getByteBuffer());
        }, (e) -> System.err.println(e));
    }

    void connection(ISession session) {
        //明文发送sessionid用于加密
    }

    void error(Throwable e) {
        System.err.println(e);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.info(event.toString());
        if (event instanceof ContextStoppedEvent) {
            server.getServer().dispose();
        }
    }
}
