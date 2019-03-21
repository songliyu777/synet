package com.synet.server.logic.process;

import com.google.protobuf.AbstractMessage;
import com.synet.message.IMessage;
import com.synet.protobuf.TestOuterClass;
import com.synet.server.logic.config.R2dbcDatabase;
import com.synet.server.logic.database.bean.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class TestProcess {

//    @Autowired
//    TestRepository testRepository;
//
//    @Autowired
//    DatabaseClient client;

    Scheduler scheduler = Schedulers.newSingle("Database Single Work");

    @Autowired
    R2dbcDatabase database;
    AtomicLong l = new AtomicLong(1);

    public Mono<IMessage<AbstractMessage>> process(IMessage<AbstractMessage> message) {
        TestOuterClass.Test test = (TestOuterClass.Test) message.getMessage();
        Test t = new Test(null, test.getName(), test.getPassword());
        //return testRepository.save(t).flatMap((entity) -> Mono.just(message));
        //client.execute().sql("INSERT INTO \"test\"(\"name\", \"password\") VALUES ('1', '1')")
        //return testRepository.save(t).flatMap((tt) -> Mono.just(message));
        database.getR2dbc().inTransaction(handle -> handle.execute("insert into test (id,name,password) values($1,$2,$3)", l.getAndIncrement(), t.getName(), t.getPassword())).subscribe();
//        return database.getR2dbc().inTransaction(handle -> handle.execute("insert into test (id,name,password) values($1,$2,$3)", l.getAndIncrement(), t.getName(), t.getPassword()))
//                .flatMap((s)->Mono.just(message)).single();

        return Mono.just(message);
    }
}
