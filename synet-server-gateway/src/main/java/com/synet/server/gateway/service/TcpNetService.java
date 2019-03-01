package com.synet.server.gateway.service;

import com.synet.TcpNetServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TcpNetService {

    TcpNetServer server ;

    @Autowired
    public TcpNetService() throws Exception {
        server = new TcpNetServer("",7000);
        server.CreateServer();
    }
}
