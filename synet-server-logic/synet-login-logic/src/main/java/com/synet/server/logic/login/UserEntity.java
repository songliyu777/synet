package com.synet.server.logic.login;

import com.synet.net.data.context.StateEntity;
import lombok.Data;

@Data
public class UserEntity implements StateEntity {
    private Long id;
    private String name;
    private Integer age;
}
