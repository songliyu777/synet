package com.synet.starter.gatewayservice;

import com.synet.net.gateway.GatewayServiceConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 网关服务器配置
 */
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "synet.gatewayservice")
public class GatewayServiceProperties extends GatewayServiceConfig {
}
