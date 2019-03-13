package com.synet.server.gateway;

import com.google.protobuf.InvalidProtocolBufferException;
import com.synet.TcpNetClient;
import com.synet.protobuf.TestOuterClass;
import com.synet.protocol.ProtocolHeadDefine;
import com.synet.protocol.TcpNetProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
        byte[] protobuf = test.toByteArray();

        for (int i = 0; i < 1; i++) {
            TcpNetClient client = new TcpNetClient("127.0.0.1", 7000);
            client.connectServer();
            client.setProcessHandler((protocol) -> {
                ByteBuf tmp = Unpooled.buffer(protocol.getSize() - protocol.getHead().headSize);
                protocol.getBody().getProtobuf(tmp);
                try {
                    TestOuterClass.Test t = TestOuterClass.Test.parseFrom(tmp.array());
                    System.out.println(t);
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                latch.countDown();
                protocol.release();
            });

            Mono<TcpNetClient> m = Mono.just(client);
            m.delaySubscription(Duration.ofMillis(i * 10))
                    .doOnSuccess((c) -> {
                        TcpNetProtocol protocol = TcpNetProtocol.create(ProtocolHeadDefine.ENCRYPT_PROTOBUF_HEAD, ProtocolHeadDefine.VERSION, 0xfffe, (short) 1, 1, protobuf);
                        c.send(protocol.toArray());
                        protocol.release();
                    })
                    .block();
        }



        Assert.assertTrue("finished", latch.await(5, TimeUnit.SECONDS));
    }
}
