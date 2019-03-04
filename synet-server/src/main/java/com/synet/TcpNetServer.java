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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.netty.Connection;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class TcpNetServer {

    CountDownLatch latch;

    private String ip = "";
    private int port = 0;
    private long readIdleTime = 0;
    private long writeIdleTime = 0;

    ChannelFuture closeFuture = null;

    Consumer<ServerBootstrap> OnBind = (param) -> {
    };
    Consumer<DisposableServer> OnBound = (param) -> {
        latch.countDown();
    };
    Consumer<DisposableServer> OnUnbound = (param) -> {
    };

    Consumer<TcpNetProtocol> process = (protocol) -> log.warn("process need implement");
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

    public void SetProcessHandler(Consumer<TcpNetProtocol> process) {
        this.process = process;
    }

    public void SetErrorHandler(Consumer<Throwable> error) {
        this.error = error;
    }

    public DisposableServer GetServer() {
        return server;
    }

    Runnable createRun = () -> {

        try {
            server = TcpServer.create().doOnBind(OnBind)
                    .doOnBound(OnBound)
                    .doOnUnbound(OnUnbound)
                    .doOnConnection((connection) -> {
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
                        connection.addHandler("server handler", new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                                Connection c = () -> ctx.channel();
                                Mono.just(c)
                                        .map(ct -> SessionManager.GetInstance().RemoveSession(ct.channel().attr(SessionManager.channel_session_id).get()))
                                        .subscribeOn(scheduler)
                                        .subscribe(doOnDisconnection, error);
                                ctx.fireChannelUnregistered();
                            }
                        });
                        connection.addHandler("frame decoder", new LengthFieldBasedFrameDecoder(1024 * 1024, 2, 4, 8, 0));
                        //连接成功调度到工作线程进行连接绑定
                        Mono.just(connection)
                                .map(c -> SessionManager.GetInstance().AddSession(SessionManager.GetInstance().NewTcpSession(c)))
                                .subscribeOn(scheduler)
                                .subscribe(doOnConnection, error);
                    })
                    .host(ip)
                    .port(port)
                    .handle((in, out) -> {
                        in.withConnection((connection) -> {
                            in.receive().map((bb) -> TcpNetProtocol.Parse(bb)
                            ).subscribe(process, error);
                        });
                        return Flux.never();
                    })
                    .wiretap(true)
                    .bind()
                    .block();
            scheduler = Schedulers.newSingle("Tcp Single Work");
            closeFuture = server.channel().closeFuture();
            closeFuture.sync();
            server.disposeNow();
            scheduler.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public void CreateServer() throws InterruptedException {
        latch = new CountDownLatch(1);
        new Thread(createRun).start();
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException();
        }
    }

    public Scheduler GetSingleWorkScheduler() {
        return scheduler;
    }

    /**
     * subscribe on work thread
     *
     * @param doOnConnection
     */
    public void DoOnConnection(Consumer<? super ISession> doOnConnection) {
        this.doOnConnection = doOnConnection;
    }

    /**
     * subscribe on work thread
     *
     * @param doOnDisconnection
     */
    public void DoOnDisconnection(Consumer<? super ISession> doOnDisconnection) {
        this.doOnDisconnection = doOnDisconnection;
    }

    public void Send(long id, byte[] data) {
        Mono.just(id)
                .map((d) -> SessionManager.GetInstance().GetTcpSession(id))
                .subscribeOn(scheduler)
                .subscribe(session -> session.Send(data), error);
    }
}
