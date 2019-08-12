package com.synet.starter.directclient;

import com.synet.starter.protobufservice.ProtobufServiceConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureAfter(value = ProtobufServiceConfiguration.class)
@EnableConfigurationProperties(value = DirectClientProperties.class)
public class DirectClientConfiguration {
    @Bean
    public DirectClientManager manager(DirectClientProperties properties) {
        return new DirectClientManager(properties.getDirectclients());
    }

    @Bean
    public DefaultDirectRemoteClient manager(DirectClientManager manager) {
        return new DefaultDirectRemoteClient(manager);
    }
}
