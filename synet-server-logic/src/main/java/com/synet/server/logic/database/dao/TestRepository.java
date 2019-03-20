package com.synet.server.logic.database.dao;


import com.synet.server.logic.database.bean.Test;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TestRepository extends ReactiveCrudRepository<Test, Long> {

}
