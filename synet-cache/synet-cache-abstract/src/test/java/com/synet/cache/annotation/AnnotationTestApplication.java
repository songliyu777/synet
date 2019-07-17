package com.synet.cache.annotation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableReactiveCaching
public class AnnotationTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnotationTestApplication.class, args);
    }


}
