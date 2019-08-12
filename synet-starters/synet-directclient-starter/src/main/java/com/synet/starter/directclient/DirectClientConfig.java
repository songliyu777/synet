package com.synet.starter.directclient;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DirectClientConfig {
    private List<String> directclients = new ArrayList<>();
}
