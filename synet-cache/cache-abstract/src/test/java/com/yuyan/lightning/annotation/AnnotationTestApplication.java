package com.yuyan.lightning.annotation;

import com.yuyan.lightning.cache.annotation.EnableReactiveCaching;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableReactiveCaching
public class AnnotationTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnnotationTestApplication.class, args);
    }


}
