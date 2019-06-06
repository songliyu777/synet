package com.synet.server.logic.database.bean;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class SequenceBean {
    @Id
    private String name;
    private int atomlong;
}
