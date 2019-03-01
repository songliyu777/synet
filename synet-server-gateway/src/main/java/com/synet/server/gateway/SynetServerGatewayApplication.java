package com.synet.server.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.synet.protocol.*;

@RestController
@SpringBootApplication
public class SynetServerGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SynetServerGatewayApplication.class, args);
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public  String Test(){
		return "Test1234";
	}
}

