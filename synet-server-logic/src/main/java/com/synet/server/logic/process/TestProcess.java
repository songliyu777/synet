package com.synet.server.logic.process;

import com.synet.server.logic.config.R2dbcDatabase;
import com.synet.server.logic.database.dao.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactivefeign.cloud.CloudReactiveFeign;
import reactivefeign.webclient.WebReactiveFeign;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class TestProcess {

    @Autowired
    TestRepository testRepository;
//
//    @Autowired
//    DatabaseClient client;

//    Scheduler[] scheduler = new Scheduler[32];

    //GatewayInterface client;

    static <T> CloudReactiveFeign.Builder<T> cloudBuilder() {
        return CloudReactiveFeign.builder(WebReactiveFeign.builder());
    }

    public TestProcess() {
//        for (int i = 0; i < 32; i++) {
//            scheduler[i] = Schedulers.newSingle("Database Single Work" + i);
//        }
//        DefaultClientConfigImpl clientConfig = new DefaultClientConfigImpl();
//        clientConfig.loadDefaultValues();
//        clientConfig.setProperty(CommonClientConfigKey.NFLoadBalancerClassName, BaseLoadBalancer.class.getName());
//        ILoadBalancer lb = null;
//        try {
//            lb = ClientFactory.registerNamedLoadBalancerFromclientConfig("server-gateway", clientConfig);
//            lb.addServers(asList(new Server("localhost", 9000)));
//        } catch (ClientException e) {
//            e.printStackTrace();
//        }
//
//
//        client = TestProcess.<GatewayInterface>cloudBuilder()
//                .enableLoadBalancer()
//                .disableHystrix()
//                .target(GatewayInterface.class, "http://server-gateway");
    }

    @Autowired
    R2dbcDatabase database;
    AtomicLong l = new AtomicLong(100000000);
//
//    public Mono<IMessage<AbstractMessage>> process(IMessage<AbstractMessage> message) {
//        TestOuterClass.Test test = (TestOuterClass.Test) message.getMessage();
//        Test t = new Test();
//        t.setId(l.getAndIncrement());
//        t.setName("test123");
//        t.setPassword("test123");
//       // Scheduler scheduler_choose = scheduler[(int) (t.getId() % 32)];
//
//        NetProtocol protocol = NetProtocol.create(ProtocolHeadDefine.ENCRYPT_PROTOBUF_HEAD,
//                ProtocolHeadDefine.VERSION,
//                message.getSerial(),
//                message.getCmd(),
//                message.getSession(),
//                message.getMessage() == null ? null : message.getMessage().toByteArray());
//
//        //testRepository.save(t).flatMap((tt)-> client.test(ByteBuffer.wrap(protocol.toArray()))).subscribe();
//        return testRepository.save(t).flatMap((tt)->Mono.just(message));
//        client.test(ByteBuffer.wrap(protocol.toArray()))
//                .doOnSuccess((buff)->{
//                        protocol.release();
//                        testRepository.saveAndFlush(t);
//                }).subscribe();
        //Mono.just(t).map((temp) -> testRepository.saveAndFlush(temp)).flatMap((tt) -> Mono.just(message)).subscribeOn(scheduler_choose);

        //return Mono.just(message);

        //return Mono.just(t).map((temp) -> testRepository.saveAndFlush(temp)).flatMap((tt) -> Mono.just(message)).subscribeOn(scheduler_choose);

        //return testRepository.save(t).flatMap((entity) -> Mono.just(message));
        //client.execute().sql("INSERT INTO \"test\"(\"name\", \"password\") VALUES ('1', '1')")
        //return testRepository.save(t).flatMap((tt) -> Mono.just(message));
        //database.getR2dbc().inTransaction(handle -> handle.execute("insert into test (id,name,password) values($1,$2,$3)", l.getAndIncrement(), t.getName(), t.getPassword())).subscribe();
//        return database.getR2dbc().inTransaction(handle -> handle.execute("insert into test (id,name,password) values($1,$2,$3)", l.getAndIncrement(), t.getName(), t.getPassword()))
//                .flatMap((s)->Mono.just(message)).single();

        // return Mono.just(message);
    //}
}
