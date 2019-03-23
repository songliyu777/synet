package com.synet.server.logic.database.dao;


import com.synet.server.logic.database.bean.Test;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends ReactiveMongoRepository<Test, Long> {

}
