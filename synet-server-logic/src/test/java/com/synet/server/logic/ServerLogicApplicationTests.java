package com.synet.server.logic;

import com.mongodb.client.result.UpdateResult;
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
import static org.springframework.data.mongodb.core.query.Query.query;

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
	public void TestRepository()
	{
		TestBean tb = new TestBean();
		tb.setId(Long.valueOf(1));
		tb.setName("123");
		tb.setPassword("123456");
		Mono<TestBean> m = repository.save(tb);
		StepVerifier.create(m)
				.expectNext(tb).verifyComplete();

	}

	@Test
	public void TestTemplate()
	{
		TestBean tb = new TestBean();
		tb.setId(Long.valueOf(1));
		tb.setName("123");
		tb.setPassword("123456");
		Mono<TestBean> m = template.insert(tb); //如果有同一样的id定义会报错
		StepVerifier.create(m).verifyError();
	}


	@Test
	public void TestTemplateInc()
	{
		//Mono<Boolean> m = template.exists(query(where("name").is("user")),"sequence");
		Update update = new Update().inc("atomlong", 1);
		Query query = Query.query(where("name").is("user"));
		template.updateFirst(query, update, "sequence").block();
//		 m.flatMap(bb -> {
//			if(bb)
//			{
//				return template.updateFirst(query, update,"sequence");
//			}
//			throw new RuntimeException();
//		}).subscribe((u)->{
//			System.out.println(u.getUpsertedId().asInt32());
//		}, e->System.err.print(e.toString()));
//		m2.subscribe((u)->{
//			System.out.println(u.getUpsertedId().asInt64());
//		}, e->System.err.print(e.toString()));
		//StepVerifier.create(m2).expectComplete().verify();
	}

}
