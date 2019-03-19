package com.synet;

import com.synet.protocol.TcpNetProtocol;
import com.synet.session.ISession;
import com.synet.session.SessionManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;
import reactor.netty.tcp.TcpServer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Slf4j
public class TcpNetServer {

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

    Consumer<TcpNetProtocol> process = (protocol) -> {
        log.warn("process need implement and protocol need release");
        protocol.release();
    };

    Consumer<Throwable> error = (throwable) -> log.error(throwable.toString());

    DisposableServer server;
    Scheduler scheduler;

    Consumer<? super ISession> doOnConnection = (session) -> log.warn("doOnConnection to be implement");
    Consumer<? super ISession> doOnDisconnection = (session) -> log.warn("doOnDisconnection to be implement");

    public TcpNetServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public TcpNetServer(String ip, int port, long readIdleTime, long writeIdleTime) {
        this.ip = ip;
        this.port = port;
        this.readIdleTime = readIdleTime;
        this.writeIdleTime = writeIdleTime;
    }

    public void setProcessHandler(Consumer<TcpNetProtocol> process) {
        this.process = process;
    }

    public void setErrorHandler(Consumer<Throwable> error) {
        this.error = error;
    }

    public DisposableServer GetServer() {
        return server;
    }

    Consumer<? super Connection> onConnection = (connection) -> {
        if (readIdleTime > 0) {
            connection.onReadIdle(readIdleTime, () -> {
                connection.disposeNow();
            });
        }
        if (writeIdleTime > 0) {
            connection.onWriteIdle(writeIdleTime, () -> {
                connection.disposeNow();
            });
        }
        connection.addHandler("server controller", new ChannelInboundHandlerAdapter() {
            @Override
            public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                //连接中断通道关闭调度到工作线程进行ISession的移除
                Connection c = () -> ctx.channel();
                long session = c.channel().attr(SessionManager.channel_session_id).get();
                Mono.just(session)
                        .map(ct -> SessionManager.GetInstance().RemoveSession(session))
                        .subscribeOn(scheduler)
                        .subscribe(doOnDisconnection, error);
                ctx.fireChannelUnregistered();
            }
        });
        connection.addHandler("frame decoder", new LengthFieldBasedFrameDecoder(1024 * 1024, 2, 4, 16, 0));
        //先生成session,再投递到工作线程
        ISession session = SessionManager.GetInstance().NewTcpSession(connection);
        //连接成功调度到工作线程进行连接绑定
        Mono.just(connection)
                .map(c -> SessionManager.GetInstance().AddSession(session))
                .subscribeOn(scheduler)
                .subscribe(doOnConnection, error);
    };

    BiFunction<? super NettyInbound, ? super
            NettyOutbound, ? extends Publisher<Void>> handler = (in, out) -> {
        in.withConnection((connection) -> {
            in.receive().map((bb) -> {
                        TcpNetProtocol protocol = TcpNetProtocol.parse(bb);
                        protocol.getHead().setSession(connection.channel().attr(SessionManager.channel_session_id).get());
                        return protocol;
                    }
            ).subscribe(process, error);
        });
        return Flux.never();
    };

    Runnable createRun = () -> {

        try {
            if (ip.isEmpty()) {
                server = TcpServer.create().doOnBind(onBind)
                        .doOnBound(onBound)
                        .doOnUnbound(onUnbound)
                        .doOnConnection(onConnection)
                        .port(port)
                        .handle(handler)
                        .wiretap(true)
                        .bind()
                        .block();
            } else {
                server = TcpServer.create().doOnBind(onBind)
                        .doOnBound(onBound)
                        .doOnUnbound(onUnbound)
                        .doOnConnection(onConnection)
                        .host(ip)
                        .port(port)
                        .handle(handler)
                        .wiretap(true)
                        .bind()
                        .block();
            }

            scheduler = Schedulers.newSingle("Tcp Single Work");
            closeFuture = server.channel().closeFuture();
            latch.countDown();
            closeFuture.sync();
            server.disposeNow();
            scheduler.dispose();
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

    public Scheduler getSingleWorkScheduler() {
        return scheduler;
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

    public void send(long id, byte[] data, Runnable onComplete) {
        Mono.just(id)
                .map((d) -> SessionManager.GetInstance().GetTcpSession(id))
                .subscribeOn(scheduler)
                .subscribe(session -> {
                    session.send(data);
                    onComplete.run();
                }, error);
    }
}
