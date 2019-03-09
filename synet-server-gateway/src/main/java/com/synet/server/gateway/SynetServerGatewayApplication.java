package com.synet.server.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class SynetServerGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SynetServerGatewayApplication.class, args);
	}
}

