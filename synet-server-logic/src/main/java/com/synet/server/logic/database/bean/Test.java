package com.synet.server.logic.database.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
public class Test {
    @Id
    Long id;
    String name;
    String password;
}
