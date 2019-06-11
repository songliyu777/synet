package com.synet.server.gateway;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TcpNetServerTest {

    @Test
    public void TcpClientTest() throws InterruptedException {
//        int connection_count = 1;
//        CountDownLatch latch_all = new CountDownLatch(connection_count);
//        AtomicInteger revccount = new AtomicInteger();
//        AtomicInteger sendcount = new AtomicInteger();
//        TestOuterClass.Test test = TestOuterClass.Test.newBuilder().setName("input 1").setPassword("input 2").build();
//        byte[] protobuf = test.toByteArray();
//
//
//        for (int i = 0; i < connection_count; i++) {
//
//            TcpNetClient client = new TcpNetClient("192.168.127.126", 8888);
//            client.setProcessHandler((protocol) -> {
//                ByteBuffer tmp = ByteBuffer.allocate(protocol.getSize() - protocol.getHead().headSize);
//                protocol.getBody().getProtobuf(tmp.array());
//                try {
//                    TestOuterClass.Test t = TestOuterClass.Test.parseFrom(tmp.array());
//                    System.err.println("recv:" + revccount.incrementAndGet() + " " + t.getName() + " " + t.getPassword());
//                } catch (InvalidProtocolBufferException e) {
//                    e.printStackTrace();
//                }
//                latch_all.countDown();
//                client.getConnection().disposeNow();
//            });
//            client.connectServer();
//
//            NetProtocol protocol = NetProtocol.create(ProtocolHeadDefine.ENCRYPT_PROTOBUF_HEAD, ProtocolHeadDefine.VERSION, 0xfffe, (short) 1, 1, protobuf);
//            byte[] temp1 = new byte[10];
//            byte[] temp2 = new byte[30];
//            byte[] temp3 = new byte[80];
//            System.arraycopy(protocol.toArray(),0,temp1,0,10);
//            System.arraycopy(protocol.toArray(),10,temp2,0,30);
//            System.arraycopy(protocol.toArray(),0,temp3,0,40);
//            System.arraycopy(protocol.toArray(),0,temp3,40,40);
//            //client.send(protocol.toArray());
//            client.send(temp3);
//            client.send(temp1);
//            Thread.sleep(1000);
//            client.send(temp2);

//            Mono<TcpNetClient> m = Mono.just(client);
//            m.delaySubscription(Duration.ofMillis(i*10))
//                    .doOnSuccess((c) -> {
//                        NetProtocol protocol = NetProtocol.create(ProtocolHeadDefine.ENCRYPT_PROTOBUF_HEAD, ProtocolHeadDefine.VERSION, 0xfffe, (short) 1, 1, protobuf);
//                        c.send(protocol.toArray());
//                        protocol.release();
//                    })
//                    .subscribe();
//        }


       // Assert.assertTrue("finished", latch_all.await(60, TimeUnit.SECONDS));
    }
}
