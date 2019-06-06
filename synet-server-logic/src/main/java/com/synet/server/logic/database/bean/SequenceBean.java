package com.synet.server.logic.database.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(value = "sequence")
@Data
public class SequenceBean {
    @Id
    private String id;
    private Long atomlong;
}
