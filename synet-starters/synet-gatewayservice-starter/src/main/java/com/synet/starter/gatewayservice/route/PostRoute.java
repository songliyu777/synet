package com.synet.starter.gatewayservice.route;

import com.synet.starter.gatewayservice.controller.PostController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class PostRoute {

    @Bean
    public RouterFunction<ServerResponse> routes(PostController postController) {
        return route(POST("/test").and(contentType(APPLICATION_OCTET_STREAM)), postController::test);
    }

    @Bean
    PostController postController() {
        return new PostController();
    }
}
