package com.synet.register;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


@SpringBootApplication
@EnableEurekaServer
public class SynetEurekaRegisterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SynetEurekaRegisterApplication.class, args);
	}

}

