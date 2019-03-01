package com.synet;

import com.synet.protocol.IProtocol;
import com.synet.protocol.TcpNetProtocol;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class MainServer {

    public static String GetThreadId() {
        return " [tid:" + Thread.currentThread().getId() + "]";
    }


    public static void main(String[] args) throws InterruptedException {
        TcpNetServer server = new TcpNetServer("",1234);
        server.CreateServer();
    }
}
