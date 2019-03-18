package com.synet.server.gateway;

import com.synet.server.gateway.configuration.FeignDefaultConfiguration;
import com.synet.server.gateway.service.TcpNetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancerAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication(exclude = ReactiveLoadBalancerAutoConfiguration.class)
@EnableEurekaClient
@EnableReactiveFeignClients(defaultConfiguration = FeignDefaultConfiguration.class)
public class SynetServerGatewayApplication {

	@Autowired
	TcpNetService tcpNetService;

	public static void main(String[] args) {
		SpringApplication.run(SynetServerGatewayApplication.class, args);
	}
}

