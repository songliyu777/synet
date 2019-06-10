package com.synet.server.logic.login.database.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(value = "user")
@Data
public class User implements Serializable {
    @Id
    private String account;
    private Long user_id;
    private String password;
}
