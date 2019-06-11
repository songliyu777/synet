package com.synet.starter.gatewayservice.handler;

import com.synet.net.protocol.NetProtocol;
import com.synet.net.route.Route;
import com.synet.net.route.RouteMatcher;
import com.synet.net.tcp.TcpServiceHandler;
import com.synet.starter.gatewayservice.service.SynetPbService;
import com.synet.starter.gatewayservice.service.SynetServiceLocator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

@Service
public class RemoteInvokeHandler extends TcpServiceHandler {

    private SynetServiceLocator synetServiceLocator;

    private RouteMatcher routeMatcher;

    public RemoteInvokeHandler(RouteMatcher routeMatcher, SynetServiceLocator synetServiceLocator) {
        this.synetServiceLocator = synetServiceLocator;
        this.routeMatcher = routeMatcher;
    }

    @Override
    public Mono<ByteBuffer> invoke(ByteBuffer byteBuffer) {
        short cmd = byteBuffer.getShort(NetProtocol.cmd_index);
        Route route = routeMatcher.match(cmd);
        SynetPbService pbService = synetServiceLocator.match(route);
        return pbService.protocol(byteBuffer);
    }
}