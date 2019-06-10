package com.synet.server.logic.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class TestRedisBean {
    @Id
    private String id;

    private String value;
}
