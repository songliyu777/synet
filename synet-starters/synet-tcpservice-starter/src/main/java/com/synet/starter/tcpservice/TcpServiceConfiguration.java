package com.synet.starter.tcpservice;

import com.synet.net.tcp.TcpService;
import com.synet.net.tcp.TcpServiceHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = TcpServiceProperties.class)
public class TcpServiceConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = TcpServiceHandler.class)
    public TcpServiceHandler synetServiceHandler() {
        return new TcpServiceHandler();
    }

    @Bean
    public TcpService synetTcpService(TcpServiceProperties properties,
                                      TcpServiceHandler serviceHandler) throws Exception {
        return new TcpService(properties, serviceHandler);
    }
}
