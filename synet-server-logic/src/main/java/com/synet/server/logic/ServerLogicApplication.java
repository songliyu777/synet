package com.synet.server.logic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ServerLogicApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerLogicApplication.class, args);
    }

}
