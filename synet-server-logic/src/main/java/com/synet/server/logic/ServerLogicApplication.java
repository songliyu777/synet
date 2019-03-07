package com.synet.server.logic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ServerLogicApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerLogicApplication.class, args);
    }

}
