package com.synet.net.tcp;

import com.synet.net.protocol.NetProtocol;
import com.synet.net.session.ISession;
import com.synet.net.session.SessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;
import reactor.netty.tcp.TcpServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Slf4j
public class TcpNetServer {

    SessionManager sessionManager;

    CountDownLatch latch;

    private String ip = "";
    private int port = 0;
    private long readIdleTime = 0;
    private long writeIdleTime = 0;

    ChannelFuture closeFuture = null;

    Consumer<ServerBootstrap> onBind = (param) -> {
    };
    Consumer<DisposableServer> onBound = (param) -> {

    };
    Consumer<DisposableServer> onUnbound = (param) -> {
    };

    Consumer<NetProtocol> process = (protocol) -> {
        log.warn("process need implement and protocol need release");
    };

    Consumer<Throwable> error = (throwable) -> log.error(throwable.toString());

    DisposableServer server;

    Consumer<? super ISession> doOnConnection = (session) -> log.warn("doOnConnection to be implement");
    Consumer<? super ISession> doOnDisconnection = (session) -> log.warn("doOnDisconnection to be implement");

    public TcpNetServer(String ip, int port, SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.ip = ip;
        this.port = port;
    }

    public TcpNetServer(String ip, int port, long readIdleTime, long writeIdleTime, SessionManager sessionManager) {
        this.ip = ip;
        this.port = port;
        this.readIdleTime = readIdleTime;
        this.writeIdleTime = writeIdleTime;
        this.sessionManager = sessionManager;
    }

    public void setProcessHandler(Consumer<NetProtocol> process) {
        this.process = process;
    }

    public void setErrorHandler(Consumer<Throwable> error) {
        this.error = error;
    }

    public DisposableServer getServer() {
        return server;
    }

    Consumer<? super Connection> onConnection = (connection) -> {
        if (readIdleTime > 0) {
            connection.onReadIdle(readIdleTime, () -> connection.disposeNow());
        }
        if (writeIdleTime > 0) {
            connection.onWriteIdle(writeIdleTime, () -> connection.disposeNow());
        }
        connection.addHandler("server controller", new ChannelInboundHandlerAdapter() {

            @Override
            public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                //连接中断通道关闭调度到工作线程进行ISession的移除
                Connection c = () -> ctx.channel();
                long session = c.channel().attr(SessionManager.channel_session_id).get();
                doOnDisconnection.accept(sessionManager.removeSession(session));
                ctx.fireChannelUnregistered();
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                    throws Exception {
                //异常处理kqueue上必须处理关闭掉，不然kqueque无法释放
                if (cause instanceof IOException) {
                    return;
                }
                ctx.close();
            }
        });
        connection.addHandler("frame decoder", new LengthFieldBasedFrameDecoder(1024 * 1024, 2, 4, 16, 0));
        ISession session = sessionManager.newTcpSession(connection);
        doOnConnection.accept(sessionManager.addSession(session));
    };

    //封包处理handler
    BiFunction<? super NettyInbound, ? super NettyOutbound, ? extends Publisher<Void>> handler = (in, out) -> {
        in.withConnection((connection) -> {
            in.receive().map((bb) -> {
                        NetProtocol protocol = NetProtocol.parse(bb);
                        protocol.getHead().setSession(connection.channel().attr(SessionManager.channel_session_id).get());
                        return protocol;
                    }
            ).subscribe(process, error);
        });
        return Flux.never();
    };

    Runnable createRun = () -> {
        try {
            TcpServer tcpServer = TcpServer.create().doOnBind(onBind)
                    .doOnBound(onBound)
                    .doOnUnbound(onUnbound)
                    .doOnConnection(onConnection)
                    .port(port)
                    .handle(handler)
                    .wiretap(true);

            if (ip != null && !ip.isEmpty()) {
                tcpServer = tcpServer.host(ip);
            }

            server = tcpServer.bind().block();
            closeFuture = server.channel().closeFuture();
            sessionManager.generateId();
            latch.countDown();
            closeFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public void createServer() throws InterruptedException {
        latch = new CountDownLatch(1);
        new Thread(createRun).start();
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException();
        }
    }

    /**
     * subscribe on work thread
     *
     * @param doOnConnection
     */
    public void doOnConnection(Consumer<? super ISession> doOnConnection) {
        this.doOnConnection = doOnConnection;
    }

    /**
     * subscribe on work thread
     *
     * @param doOnDisconnection
     */
    public void doOnDisconnection(Consumer<? super ISession> doOnDisconnection) {
        this.doOnDisconnection = doOnDisconnection;
    }

    /**
     * send byte on work thread
     *
     * @param id
     * @param buffer
     */
    public void send(long id, ByteBuffer buffer) {
        ISession session = sessionManager.getTcpSession(id);
        session.send(buffer.array());
    }
}
