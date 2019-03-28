//package com.synet.server.logic.config;
//
//import com.synet.server.logic.controller.PostHandler;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.reactive.config.EnableWebFlux;
//import org.springframework.web.reactive.function.server.RouterFunction;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
//import static org.springframework.web.reactive.function.server.RouterFunctions.route;
//import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
//import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
//
//@Configuration
//@EnableWebFlux
//public class RouteConfig {
//    @Bean
//    public RouterFunction<ServerResponse> routes(PostHandler postHandler){
//        return route(POST("/test").and(contentType(APPLICATION_OCTET_STREAM)), postHandler::test);
//    }
//}
