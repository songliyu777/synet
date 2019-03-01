package com.synet.server.gateway.handler;

import com.synet.server.gateway.service.TcpNetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageHandler {

    @Autowired
    TcpNetService tcpService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public  String Test(){
        return "Test1234";
    }
}
