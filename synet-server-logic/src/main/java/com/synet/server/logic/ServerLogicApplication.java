package com.synet.server.logic;

import com.synet.starter.feign.FeignClientsConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancerAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication(exclude = ReactiveLoadBalancerAutoConfiguration.class)
@EnableEurekaClient
@EnableReactiveFeignClients(defaultConfiguration = FeignClientsConfiguration.class)
public class ServerLogicApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerLogicApplication.class, args);
    }

}
