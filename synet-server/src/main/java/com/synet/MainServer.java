package com.synet;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.tcp.TcpServer;

import java.time.Duration;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class MainServer {

    public static String GetThreadId() {
        return " [tid:" + Thread.currentThread().getId() + "]";
    }


    public static void main(String[] args) {
        System.out.println("==>0:" + " Thread id:" + Thread.currentThread().getId() + "name:" + Thread.currentThread().getName());
        Consumer<ServerBootstrap> test1 = (param) -> {
            System.out.println("==>1:" + " Thread id:" + Thread.currentThread().getId() + "name:" + Thread.currentThread().getName());
        };
        Consumer<DisposableServer> test2 = (param) -> System.out.println("==>2:" + " Thread id:" + Thread.currentThread().getId() + "name:" + Thread.currentThread().getName());
        Consumer<DisposableServer> test3 = (param) -> System.out.println("==>3:" + " Thread id:" + Thread.currentThread().getId() + "name:" + Thread.currentThread().getName());

        try {

            DisposableServer server = TcpServer.create().doOnBind(test1)
                    .doOnBound(test2)
                    .doOnUnbound(test3)
                    .host("127.0.0.1")
                    .port(1234)
                    .bind()
                    .block();
            ChannelFuture channelFuture = server.channel()
                    .closeFuture();
//                    .addListener(ChannelFutureListener.CLOSE);
//            for (int i = 1; i <= 100; i++) {
//                Mono<Integer> mono = Mono.just(new Integer(i));
//                if (i == 100) {
//                    mono.delaySubscription(Duration.ofSeconds(3))
//                            .doOnSuccess((input) -> {
//                                System.out.println("==>end success id:" + input + " Thread id:" + Thread.currentThread().getId() + "name:" + Thread.currentThread().getName());
//                                channelFuture.channel().close();
//                            })
//                            .subscribe((input) -> System.out.println("==>end mono:" + input + " Thread id:" + Thread.currentThread().getId() + "name:" + Thread.currentThread().getName()));
//                } else {
//                    mono.delaySubscription(Duration.ofSeconds(3))
//                            .doOnSuccess((input) -> System.out.println("==>success id:" + input + " Thread id:" + Thread.currentThread().getId() + "name:" + Thread.currentThread().getName()))
//                            .subscribe((input) -> System.out.println("==>mono:" + input + " Thread id:" + Thread.currentThread().getId() + "name:" + Thread.currentThread().getName()));
//                }
//
////                Mono.create(sink -> {
////                    System.out.println("==>exi process id:" + i + " Thread id:" + Thread.currentThread().getId());
////                    if (i == 100) {
////                        channelFuture.channel().close();
////                    }
////                    sink.success();
////                }).doOnSuccess((param) -> System.out.println("==>exi success id:" + i + " Thread id:" + Thread.currentThread().getId()))
////                        .delaySubscription(Duration.ofSeconds(3)).block();
//            }

            channelFuture.sync();
            System.out.println("disposeNow");
            server.disposeNow();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("==>6:" + Thread.currentThread().getId() + "name:" + Thread.currentThread().getName());
    }
}
