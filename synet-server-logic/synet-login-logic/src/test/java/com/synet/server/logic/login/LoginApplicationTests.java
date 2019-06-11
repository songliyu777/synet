package com.synet.server.logic.login;

import com.synet.cache.lock.RedisLock;
import com.synet.cache.lock.RedisLockFactory;
import com.synet.server.logic.login.database.bean.Sequence;
import com.synet.server.logic.login.database.bean.User;
import com.synet.server.logic.login.database.dao.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginApplicationTests {

    @Autowired
    ReactiveMongoTemplate template;

    @Autowired
    UserDao userDao;

    @Autowired
    ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    @Autowired
    RedisLockFactory redisLockFactory;

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
        Mono<User> m2 = m.switchIfEmpty(Mono.just(new User()));
        StepVerifier.create(m2).consumeNextWith(System.out::println).verifyComplete();
    }

    @Test
    public void TestUserDao_delete() {
        Mono<User> m = userDao.delete("111");
        StepVerifier.create(m).consumeNextWith(System.out::println).verifyComplete();
    }

    @Test
    public void TestReactiveRedisTemplate()
    {
        Mono<Long> m = reactiveStringRedisTemplate.opsForSet().add("test1","123456");
        StepVerifier.create(m).consumeNextWith(System.out::println).verifyComplete();
    }

    @Test
    public void TestRedisLock_1() {
        RedisLock rl = redisLockFactory.NewLock(Long.valueOf(5000));
        Mono<Boolean> m = rl.lock("Test").filter(b -> b).flatMap(b->Mono.just("LockTest").delaySubscription(Duration.ofMillis(4000))).flatMap(s -> rl.unlock("Test"));
        StepVerifier.create(m).consumeNextWith(System.out::println).verifyComplete();
    }

    @Test
    public void TestRedisLock_2() {
        RedisLock rl1 = redisLockFactory.NewLock(Long.valueOf(5000));
        Mono<String> m1 = rl1.lock("Test").filter(b -> b).flatMap(b->Mono.just("LockTest1:"+ System.currentTimeMillis()).delaySubscription(Duration.ofMillis(2000)));
        StepVerifier.create(m1).consumeNextWith(System.out::println).verifyComplete();
        //无法拿到锁
        RedisLock rl2 = redisLockFactory.NewLock(Long.valueOf(5000));
        Mono<String> m2 = rl2.lock("Test").doOnSuccess(System.err::println).filter(b -> b).flatMap(b->Mono.just("LockTest2:" + System.currentTimeMillis()));
        StepVerifier.create(m2).verifyComplete();
    }
}
