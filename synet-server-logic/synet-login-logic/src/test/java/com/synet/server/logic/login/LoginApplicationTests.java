package com.synet.server.logic.login;

import com.synet.server.logic.login.database.bean.Sequence;
import com.synet.server.logic.login.database.bean.User;
import com.synet.server.logic.login.redis.RedisKeyDefine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
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
    ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    @Autowired
    ReactiveRedisTemplate<String, User> reactiveRedisTemplate;

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
    public void TestUserDao_Transactional_Double() {
        for (int i = 0; i < 100; i++) {
            User user1 = new User();
            user1.setAccount("111");
            user1.setUser_id(Long.valueOf(1));

            User user2 = new User();
            user2.setAccount("222");
            user2.setUser_id(Long.valueOf(2));

            template.inTransaction().execute(action ->
                    action.save(user1).doOnError(System.err::println).then(action.save(user2)).flatMap(val -> {
                        return Mono.just(user2);
                    })).as(StepVerifier::create).consumeNextWith(System.err::println).verifyComplete();
        }

    }

    @Test
    public void TestReactiveRedisTemplate() {
        Mono<Long> m = reactiveStringRedisTemplate.opsForSet().add("test1", "123456");
        StepVerifier.create(m).consumeNextWith(System.out::println).verifyComplete();
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
