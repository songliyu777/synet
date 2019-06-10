package com.synet.server.logic.login.database.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

public class TestReactiveMongoTemplate {
    @Autowired
    ReactiveMongoTemplate template;
}
