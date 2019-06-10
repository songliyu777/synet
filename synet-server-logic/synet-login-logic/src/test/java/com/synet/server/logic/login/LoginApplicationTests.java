package com.synet.server.logic.login;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import com.synet.server.logic.login.database.bean.Sequence;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginApplicationTests {

    @Autowired
    ReactiveMongoTemplate template;

    @Test
    public void TestTemplateInc() {
        Query query = Query.query(where("_id").is("user"));
        Update update = new Update().inc("atomlong", Long.valueOf(1));
        Mono<Boolean> m = template.exists(query, "sequence");
        Mono<Sequence> m2 = m.flatMap(b -> {
            if (b) {
                return template.findAndModify(query, update,new FindAndModifyOptions().returnNew(true), Sequence.class);
            }
            Sequence s = new Sequence();
            s.setId("user");
            s.setAtomlong(Long.valueOf(1));
            return template.insert(s);
        });

        StepVerifier.create(m2).consumeNextWith(r -> {
            System.out.println(r);
        }).verifyComplete();
    }
}
