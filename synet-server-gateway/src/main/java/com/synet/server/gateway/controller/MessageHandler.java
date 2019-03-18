package com.synet.server.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageHandler {

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public  String Test(){
        return "Test1234";
    }
}
