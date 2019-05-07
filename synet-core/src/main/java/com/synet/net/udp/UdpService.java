package com.synet.net.udp;

import com.synet.net.service.NetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.nio.ByteBuffer;

@Slf4j
public class UdpService implements ApplicationListener, NetService {
    @Override
    public void send(long id, ByteBuffer buffer) {

    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

    }
}
