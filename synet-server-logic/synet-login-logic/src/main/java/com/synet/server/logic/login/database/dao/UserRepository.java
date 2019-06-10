package com.synet.server.logic.login.database.dao;

import com.synet.server.logic.login.database.bean.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
}

