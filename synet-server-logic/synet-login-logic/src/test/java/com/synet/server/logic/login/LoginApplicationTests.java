package com.synet.server.logic.login;

import com.synet.server.logic.login.database.bean.Sequence;
import com.synet.server.logic.login.database.bean.User;
import com.synet.server.logic.login.database.dao.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginApplicationTests {

    @Autowired
    ReactiveMongoTemplate template;

    @Autowired
    UserDao userDao;

    @Test
    public void TestTemplateInc() {
        Query query = Query.query(where("_id").is("user"));
        Update update = new Update().inc("atomlong", Long.valueOf(1));
        Mono<Boolean> m = template.exists(query, "sequence");
        Mono<Sequence> m2 = m.flatMap(b -> {
            if (b) {
                return template.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), Sequence.class);
            }
            Sequence s = new Sequence();
            s.setId("user");
            s.setAtomlong(Long.valueOf(1));
            return template.insert(s);
        });

        StepVerifier.create(m2).consumeNextWith(System.out::println).verifyComplete();
    }

    @Test
    public void TestUserDao() {
        User user = new User();
        user.setAccount("111");
        user.setUser_id(Long.valueOf(111));
        Mono<User> m = userDao.save(user);
        StepVerifier.create(m).consumeNextWith(System.out::println).verifyComplete();
    }

    @Test
    public void TestUserDao_find_one() {
        Mono<User> m = userDao.findOne("111");
        StepVerifier.create(m).consumeNextWith(System.out::println).verifyComplete();
    }


    @Test
    public void TestUserDao_delete() {
        Mono<Void> m = userDao.delete("111");
        StepVerifier.create(m).verifyComplete();
    }
}
