package com.synet;

import com.synet.protocol.TcpNetProtocol;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import reactor.core.publisher.Flux;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

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

    Consumer<TcpNetProtocol> success;
    Consumer<Throwable> error;

    DisposableServer server;

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
        this.success = success;
        this.error = error;
    }

    Runnable createRun = () -> {

        try {
            server = TcpServer.create().doOnBind(OnBind)
                    .doOnBound(OnBound)
                    .doOnUnbound(OnUnbound)
                    .doOnConnection((c) -> {
                        if (readIdleTime > 0) {
                            c.onReadIdle(readIdleTime, () -> {
                                c.disposeNow();
                            });
                        }
                        if (writeIdleTime > 0) {
                            c.onWriteIdle(writeIdleTime, () -> {
                                c.disposeNow();
                            });
                        }
                        c.addHandler("frame", new LengthFieldBasedFrameDecoder(1024 * 1024, 2, 4, 8, 0));
                    })
                    .host(ip)
                    .port(port)
                    .handle((in, out) -> {
                        in.receive().map((bb) -> {
                            try {
                                return TcpNetProtocol.Parse(bb);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }).subscribe((protocol) -> System.out.println("Success"), System.err::println);
                        return Flux.never();
                    })
                    .wiretap(true)
                    .bind()
                    .block();

            closeFuture = server.channel().closeFuture();
            closeFuture.sync();
            server.disposeNow();
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
}
