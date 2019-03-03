package com.synet;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TcpNetClient {

    private String ip = "";
    private int port = 0;

    Connection client;

    CountDownLatch latch;

    Consumer<Bootstrap> OnConnect = (param) -> {
    };
    Consumer<Connection> OnConnected = (param) -> {
        latch.countDown();
    };
    Consumer<Connection> OnDisconnected = (param) -> {
        System.err.println("Disconnected");
    };

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
                    .connect()
                    .block();

            ChannelFuture closeFuture = client.channel().closeFuture();
            closeFuture.sync();
            client.disposeNow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    public void ConnectServer() throws InterruptedException {
        latch = new CountDownLatch(1);
        new Thread(connect).start();
        if (!latch.await(5, TimeUnit.SECONDS)) {
            throw new InterruptedException();
        }
    }

    public void Send(byte[] data) {
        ByteBufFlux f = ByteBufFlux.fromInbound(ByteBufFlux.just(data));
        client.outbound().send(f).then().subscribe();
    }
}
