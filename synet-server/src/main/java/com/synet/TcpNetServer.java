package com.synet;

import com.synet.protocol.TcpNetProtocol;
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

    Consumer<? super Connection> doOnConnection = (connection) -> log.warn("==>doOnConnection need implement" + GetThreadId());
    Consumer<? super Connection> doOnDisconnection = (connection) -> log.warn("doOnDisconnection need implement" + GetThreadId());

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

    public void SetProcessHandler(Consumer<TcpNetProtocol> process, Consumer<Throwable> error) {
        this.process = process;
        this.error = error;
    }

    public String GetThreadId() {
        return " [tid:" + Thread.currentThread().getName() + "]";
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
                                Mono.just(c).subscribeOn(scheduler).subscribe(doOnDisconnection);
                                ctx.fireChannelUnregistered();
                            }
                        });
                        connection.addHandler("frame decoder", new LengthFieldBasedFrameDecoder(1024 * 1024, 2, 4, 8, 0));
                        Mono.just(connection).subscribeOn(scheduler).subscribe(doOnConnection);
                    })
                    .host(ip)
                    .port(port)
                    .handle((in, out) -> {
                        in.withConnection((connection) -> {
                            in.receive().map((bb) -> {
                                try {
                                    return TcpNetProtocol.Parse(bb);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }).subscribe(process, error);
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

    public void DoOnConnection(Consumer<? super Connection> doOnConnection) {
        this.doOnConnection = doOnConnection;
    }

    public void DoOnDisconnection(Consumer<? super Connection> doOnDisconnection) {
        this.doOnDisconnection = doOnDisconnection;
    }
}
