package com.synet.starter.tcpservice;

import com.synet.net.session.SessionManager;
import com.synet.net.tcp.TcpNetServer;
import com.synet.net.tcp.TcpService;
import com.synet.net.tcp.TcpServiceHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.tcp.TcpServer;

@Configuration
@EnableConfigurationProperties(value = TcpServiceProperties.class)
public class TcpServiceConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = TcpServiceHandler.class)
    public TcpServiceHandler synetServiceHandler() {
        return new TcpServiceHandler();
    }

    @Bean
    public SessionManager synetSessionManager(){return new SessionManager();}

    @Bean
    public TcpNetServer synetTcpServer(TcpServiceProperties properties, SessionManager sessionManager) {
        return new TcpNetServer(properties.getHost(), properties.getPort(), properties.getReadIdleTime(), properties.getWriteIdleTime(), sessionManager);
    }

    @Bean
    public TcpService synetTcpService(TcpNetServer server, TcpServiceHandler serviceHandler) throws Exception {
        return new TcpService(server, serviceHandler);
    }
}
