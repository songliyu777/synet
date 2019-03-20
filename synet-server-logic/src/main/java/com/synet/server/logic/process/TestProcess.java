package com.synet.server.logic.process;

import com.google.protobuf.AbstractMessage;
import com.synet.message.IMessage;
import com.synet.protobuf.TestOuterClass;
import com.synet.server.logic.database.bean.Test;
import com.synet.server.logic.database.dao.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.function.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TestProcess {

    @Autowired
    TestRepository testRepository;

    @Autowired
    DatabaseClient client;

    public Mono<IMessage<AbstractMessage>> process(IMessage<AbstractMessage> message) {
        TestOuterClass.Test test = (TestOuterClass.Test) message.getMessage();
        Test t = new Test(null, test.getName(), test.getPassword());
        //return testRepository.save(t).flatMap((entity) -> Mono.just(message));
        //client.execute().sql("INSERT INTO \"test\"(\"name\", \"password\") VALUES ('1', '1')")
        return testRepository.save(t).flatMap((tt) -> Mono.just(message));
    }
}
