package com.synet.server.logic.login.database.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "sequence")
@Data
public class SequenceBean {
    @Id
    private String id;
    private Long atomlong;
}
