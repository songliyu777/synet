package com.synet;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.ByteBufFlux;
import reactor.netty.Connection;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;
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

    public static void main(String[] args) throws InterruptedException {
        byte a[] = {(byte) 0xff, (byte) 0xff, 0x00, 0x00, 0x00, 0x0a, 0x00, 0x00, 0x00, 0x01, (byte) 0xff, (byte) 0xfe, (byte) 0xe7, (byte) 0x04, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0x10};


        for (int i = 0; i < 10; i++) {
            TcpNetClient client = new TcpNetClient("127.0.0.1", 1234);

            client.ConnectServer();

            Mono<Integer> m = Mono.just(i);
            m.delaySubscription(Duration.ofMillis(i*100))
                    .doOnSuccess((t) -> client.Send(a))
                    .block();
        }

    }
}
