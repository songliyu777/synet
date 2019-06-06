package com.synet.server.logic.database.bean;


import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class TestBean {

    private String id;
    private String name;
    private String password;
}
