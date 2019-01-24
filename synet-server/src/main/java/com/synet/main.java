package com.synet;

import reactor.netty.tcp.TcpServer;

public class main {
    public static void main(String[] args) {
        TcpServer.create()
                .host("127.0.0.1")
                .port(1234)
                .bind()
                .block();
    }
}
