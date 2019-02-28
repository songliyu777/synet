package com.synet;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.scheduler.Schedulers;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

import java.util.function.Consumer;

public class MainClient {
    public static String GetThreadId() {
        return " [tid:" + Thread.currentThread().getId() + "]";
    }

    public static class SampleSubscriber<T> extends BaseSubscriber<T> {

        public void hookOnSubscribe(Subscription subscription) {
            System.out.println("Subscribed" + GetThreadId());
            request(1);
        }

        public void hookOnNext(T value) {
            System.out.println(value + GetThreadId());
            request(1);
        }
    }

    public static void main(String[] args) {
        byte a[] = {(byte) 0xff, (byte) 0xff, 0x00, 0x00, 0x00, 0x0a, 0x00, 0x00, 0x00, 0x01, (byte) 0xff, (byte) 0xfe, (byte) 0xe7, (byte) 0x04, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0x10};

        System.out.println("==>0:" + " Thread id:" + Thread.currentThread().getId() + "name:" + Thread.currentThread().getName());
        Consumer<Bootstrap> test1 = (param) -> {
            System.out.println("==>1:" + " Thread id:" + Thread.currentThread().getId() + "name:" + Thread.currentThread().getName());
        };
        SampleSubscriber<ByteBuf> ss = new SampleSubscriber<ByteBuf>();

        Consumer<Connection> test2 = (param) -> {
            System.out.println("send" + GetThreadId());
            ByteBufFlux f = ByteBufFlux.fromInbound(ByteBufFlux.just(a));
            f.subscribe(ss);
            param.outbound().send(f).send(f).then().subscribe().dispose();
        };
        Consumer<Connection> test3 = (param) -> System.out.println("==>Disconnected:" + " Thread id:" + Thread.currentThread().getId() + "name:" + Thread.currentThread().getName());

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
