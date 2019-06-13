package com.synet.server.logic.login;

import com.mongodb.MongoClient;
import com.synet.cache.lock.RedisLock;
import com.synet.cache.lock.RedisLockFactory;
import com.synet.server.logic.login.database.bean.Sequence;
import com.synet.server.logic.login.database.bean.User;
import com.synet.server.logic.login.database.dao.UserDao;
import com.synet.server.logic.login.redis.RedisKeyDefine;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
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
    ReactiveRedisTemplate<String, User> reactiveRedisTemplate;

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
    public void TestUserDao_Transactional() {
        User user = new User();
        user.setAccount("111");
        user.setUser_id(Long.valueOf(1));

        //不能嵌入repository这一点要切记了，下面代码会回滚
        template.inTransaction().execute(action ->
                action.save(user).then(action.insert(user)).flatMap(val -> {
                    return Mono.just(user);
                })).as(StepVerifier::create).verifyError();
    }

    @Test
    public void TestReactiveRedisTemplate() {
        Mono<Long> m = reactiveStringRedisTemplate.opsForSet().add("test1", "123456");
        StepVerifier.create(m).consumeNextWith(System.out::println).verifyComplete();
    }

    @Test
    public void TestRedisLock_1() {
        RedisLock rl = redisLockFactory.NewLock(Long.valueOf(5000));
        Mono<Boolean> m = rl.lock("Test").filter(b -> b).flatMap(b -> Mono.just("LockTest").delaySubscription(Duration.ofMillis(4000))).flatMap(s -> rl.unlock("Test"));
        StepVerifier.create(m).consumeNextWith(System.out::println).verifyComplete();
    }

    @Test
    public void TestRedisLock_2() {
        RedisLock rl1 = redisLockFactory.NewLock(Long.valueOf(5000));
        Mono<String> m1 = rl1.lock("Test").filter(b -> b).flatMap(b -> Mono.just("LockTest1:" + System.currentTimeMillis()).delaySubscription(Duration.ofMillis(2000)));
        StepVerifier.create(m1).consumeNextWith(System.out::println).verifyComplete();
        //无法拿到锁
        RedisLock rl2 = redisLockFactory.NewLock(Long.valueOf(5000));
        Mono<String> m2 = rl2.lock("Test").doOnSuccess(System.err::println).filter(b -> b).flatMap(b -> Mono.just("LockTest2:" + System.currentTimeMillis()));
        StepVerifier.create(m2).verifyComplete();
    }

    @Test
    public void TestRedisList() {
        User user = new User();
        user.setAccount("123456");
        user.setPassword("123456");
        Mono<Long> m = reactiveRedisTemplate.opsForList().size(RedisKeyDefine.LOGIN_QUEUE).flatMap(len -> reactiveRedisTemplate.opsForList().leftPush(RedisKeyDefine.LOGIN_QUEUE, user));
        StepVerifier.create(m).consumeNextWith(System.out::println).verifyComplete();
    }

    @Test
    public void TestRedisQueue() {
        User user = new User();
        user.setAccount("123456");
        user.setPassword("123456");
        Mono<Long> m = reactiveRedisTemplate.opsForSet().add(RedisKeyDefine.LOGIN_SET, user)
                .flatMap(i -> {
                    if (i > 0) {
                        return reactiveRedisTemplate.opsForList().leftPush(RedisKeyDefine.LOGIN_QUEUE, user);
                    }
                    return Mono.just(i);
                });
//        Mono<Long> m = reactiveRedisTemplate.opsForSet().isMember(RedisKeyDefine.LOGIN_SET, user)
//                .filter(b->!b)
//                .flatMap(b->reactiveRedisTemplate.opsForSet().add(RedisKeyDefine.LOGIN_SET, user));
        StepVerifier.create(m).consumeNextWith(System.out::println).verifyComplete();
    }
}
