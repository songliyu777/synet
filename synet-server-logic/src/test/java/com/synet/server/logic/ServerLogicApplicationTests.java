package com.synet.server.logic;

import com.mongodb.bulk.UpdateRequest;
import com.synet.server.logic.database.bean.SequenceBean;
import com.synet.server.logic.database.bean.TestBean;
import com.synet.server.logic.database.dao.TestRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServerLogicApplicationTests {

	@Autowired
	ReactiveMongoTemplate template;

	@Autowired
	TestRepository repository;

	@Test
	public void contextLoads() {
	}

	@Test
	public void TestRepository() {
		TestBean tb = new TestBean();
		tb.setName("123");
		tb.setPassword("123456");
		Mono<TestBean> m = repository.save(tb);//如果有同一样的id是更新
		StepVerifier.create(m).consumeNextWith(System.out::println).verifyComplete();
	}

	@Test
	public void TestTemplate()
	{
		TestBean tb = new TestBean();
		tb.setName("123");
		tb.setPassword("123456");
		Mono<TestBean> m = template.insert(tb); //如果有同一样的id定义会报错
		StepVerifier.create(m).consumeNextWith(System.out::println).verifyComplete();
	}

	@Test
	public void TestTemplateInc()
	{
		Query query = Query.query(where("_id").is("user"));
		Update update = new Update().inc("atomlong", Long.valueOf(1));
		Mono<Boolean> m = template.exists(query,"sequence");
		Mono<Object> m2 = m.flatMap(b -> {
			if (b) {
				return template.updateFirst(query, update, "sequence");
			}
			SequenceBean s = new SequenceBean();
			s.setId("user");
			s.setAtomlong(Long.valueOf(1));
			return template.insert(s);
		});

		StepVerifier.create(m2).consumeNextWith(System.out::println).verifyComplete();
	}

}
