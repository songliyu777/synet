package com.synet.server.gateway.service;

import com.synet.TcpNetServer;
import com.synet.protocol.TcpNetProtocol;
import com.synet.server.gateway.protocol.ProtocolHeadDefine;
import com.synet.session.ISession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Slf4j
@Service
public class TcpNetService {

    TcpNetServer server;

    Consumer<TcpNetProtocol> process = protocol -> {
    };
    Consumer<Throwable> error = error -> {
        log.error(error.toString());
    };
    Consumer<? super ISession> doOnConnection = session -> {

        TcpNetProtocol.create(ProtocolHeadDefine.ENCRYPT_PROTOBUF, ProtocolHeadDefine.VERSION, 0, 0, (short) 0, null, 0);
    };

    @Autowired
    public TcpNetService() throws Exception {
        server = new TcpNetServer("", 7000, 60000, 120000);
        server.SetProcessHandler(process);
        server.SetErrorHandler(error);
        server.DoOnConnection(doOnConnection);
        server.CreateServer();
    }
}
