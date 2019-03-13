package com.synet;

import com.synet.protocol.TcpNetProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class TcpNetClient {

    private String ip = "";
    private int port = 0;

    Connection client;

    CountDownLatch latch;

    Consumer<Bootstrap> OnConnect = (bootstrap) -> {
    };
    Consumer<Connection> OnConnected = (connection) -> {
        connection.addHandler("frame decoder", new LengthFieldBasedFrameDecoder(1024 * 1024, 2, 4, 16, 0));
        latch.countDown();
    };
    Consumer<Connection> OnDisconnected = (connection) -> {
        System.err.println("Disconnected");
    };

    Consumer<TcpNetProtocol> process = (protocol) -> {
        log.warn("process need implement and protocol need release");
        protocol.release();
    };

    Consumer<Throwable> error = (throwable) -> log.error(throwable.toString());

    public TcpNetClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    Runnable connect = () -> {
        try {
            client = TcpClient.create()
                    .doOnConnect(OnConnect)
                    .doOnConnected(OnConnected)
                    .doOnDisconnected(OnDisconnected)
                    .host(ip)
                    .port(port)
                    .handle((in, out) ->{
                        in.withConnection((connection) -> {
                            in.receive().map((bb) -> TcpNetProtocol.parse(bb)
                            ).subscribe(process, error);
                        });
                        return Flux.never();
                    })
                    .connect()
                    .block();

            ChannelFuture closeFuture = client.channel().closeFuture();
            closeFuture.sync();
            client.disposeNow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public void connectServer() throws InterruptedException {
        latch = new CountDownLatch(1);
        new Thread(connect).start();
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException();
        }
    }

    public void send(byte[] data) {
        ByteBufFlux f = ByteBufFlux.fromInbound(Flux.just(data));
        client.outbound().send(f).then().subscribe();
    }

    public Connection getConnection() {
        return client;
    }

    public void setProcessHandler(Consumer<TcpNetProtocol> process) {
        this.process = process;
    }

    public void setErrorHandler(Consumer<Throwable> error) {
        this.error = error;
    }
}
