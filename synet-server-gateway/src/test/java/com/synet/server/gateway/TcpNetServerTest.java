package com.synet.server.gateway;

import com.synet.TcpNetClient;
import com.synet.protobuf.TestOuterClass;
import com.synet.protocol.TcpNetProtocol;
import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TcpNetServerTest {

    @Test
    public void TcpClientTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        TestOuterClass.Test test = TestOuterClass.Test.newBuilder().setName("input 1").setPassword("input 2").build();
        byte[] send = test.toByteArray();
        byte a[] = {(byte) 0xff, (byte) 0xff, 0x00, 0x00, 0x00, 0x0a, 0x00, 0x00, 0x00, 0x01, (byte) 0xff, (byte) 0xfe, (byte) 0xe7, (byte) 0x04, 0x1, 0x2, 0x3, 0x4, 0x5, 0x6, 0x7, 0x8, 0x9, 0x10};

        for (int i = 0; i < 1; i++) {
            TcpNetClient client = new TcpNetClient("127.0.0.1", 7000);
            client.connectServer();
            Mono<TcpNetClient> m = Mono.just(client);
            m.delaySubscription(Duration.ofMillis(i * 10))
                    .doOnSuccess((c) -> {
                        TcpNetProtocol protocol = TcpNetProtocol.create((byte) 0xff, (byte) 0xff, 0xfffe, (short)1, send);
                        c.send(protocol.toArray());
                        protocol.release();
                    })
                    .block();
        }

        Thread.sleep(6000000);
        latch.countDown();

        Assert.assertTrue("finished", latch.await(5, TimeUnit.SECONDS));
    }
}
