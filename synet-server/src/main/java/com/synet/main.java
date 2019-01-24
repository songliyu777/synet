package com.synet;

import io.netty.bootstrap.ServerBootstrap;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

import java.util.function.Consumer;

public class main {
    public static void main(String[] args) {

        Consumer<ServerBootstrap> test1 = (param)->System.out.println("==>1");
        Consumer<DisposableServer> test2 = (param)->System.out.println("==>2");
        Consumer<DisposableServer> test3 = (param)->System.out.println("==>2");

        TcpServer.create()
                .doOnBind(test1)
                .doOnBound(test2)
                .doOnUnbound(test3)
                .host("127.0.0.1")
                .port(1234)
                .bind()
                .block();

    }
}
