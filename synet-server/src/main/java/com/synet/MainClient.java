package com.synet;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

import java.util.function.Consumer;

public class MainClient {
    public static String GetThreadId() {
        return " [tid:" + Thread.currentThread().getId() + "]";
    }
    public static void main(String[] args) {
        System.out.println("==>0:" + " Thread id:" + Thread.currentThread().getId() + "name:" + Thread.currentThread().getName());
        Consumer<Bootstrap> test1 = (param) -> {
            System.out.println("==>1:" + " Thread id:" + Thread.currentThread().getId() + "name:" + Thread.currentThread().getName());
        };
        Consumer<Connection> test2 = (param) -> System.out.println("==>2:" + " Thread id:" + Thread.currentThread().getId() + "name:" + Thread.currentThread().getName());
        Consumer<Connection> test3 = (param) -> System.out.println("==>3:" + " Thread id:" + Thread.currentThread().getId() + "name:" + Thread.currentThread().getName());

        Connection client = TcpClient.create()
                .doOnConnect(test1)
                .doOnConnected(test2)
                .doOnDisconnected(test3)
                .host("127.0.0.1")
                .port(1234)
                .connect()
                .block();

        ChannelFuture channelFuture = client.channel().closeFuture();
        try {
            channelFuture.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("disposeNow");

    }
}
