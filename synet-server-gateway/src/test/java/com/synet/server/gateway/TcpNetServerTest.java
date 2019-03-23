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

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TcpNetServerTest {

    @Test
    public void TcpClientTest() throws InterruptedException {
        int connection_count = 1;
        CountDownLatch latch_all = new CountDownLatch(connection_count);
        AtomicInteger revccount = new AtomicInteger();
        AtomicInteger sendcount = new AtomicInteger();
        TestOuterClass.Test test = TestOuterClass.Test.newBuilder().setName("input 1").setPassword("input 2").build();
        byte[] protobuf = test.toByteArray();


        for (int i = 0; i < connection_count; i++) {

            TcpNetClient client = new TcpNetClient("127.0.0.1", 7000);
            client.setProcessHandler((protocol) -> {
                ByteBuffer tmp = ByteBuffer.allocate(protocol.getSize() - protocol.getHead().headSize);
                protocol.getBody().getProtobuf(tmp.array());
                try {
                    TestOuterClass.Test t = TestOuterClass.Test.parseFrom(tmp.array());
                    System.err.println("recv:" + revccount.incrementAndGet() + " " + t.getName() + " " + t.getPassword());
                } catch (InvalidProtocolBufferException e) {
                    e.printStackTrace();
                }
                latch_all.countDown();
                client.getConnection().disposeNow();
            });
            client.connectServer();

            TcpNetProtocol protocol = TcpNetProtocol.create(ProtocolHeadDefine.ENCRYPT_PROTOBUF_HEAD, ProtocolHeadDefine.VERSION, 0xfffe, (short) 1, 1, protobuf);
            client.send(protocol.toArray());

//            Mono<TcpNetClient> m = Mono.just(client);
//            m.delaySubscription(Duration.ofMillis(i*10))
//                    .doOnSuccess((c) -> {
//                        TcpNetProtocol protocol = TcpNetProtocol.create(ProtocolHeadDefine.ENCRYPT_PROTOBUF_HEAD, ProtocolHeadDefine.VERSION, 0xfffe, (short) 1, 1, protobuf);
//                        c.send(protocol.toArray());
//                        protocol.release();
//                    })
//                    .subscribe();
        }


        Assert.assertTrue("finished", latch_all.await(60, TimeUnit.SECONDS));
    }
}
