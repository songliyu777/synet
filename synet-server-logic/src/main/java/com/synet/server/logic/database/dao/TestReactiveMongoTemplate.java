package com.synet.server.logic.database.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

public class TestReactiveMongoTemplate {
    @Autowired
    ReactiveMongoTemplate template;
}
