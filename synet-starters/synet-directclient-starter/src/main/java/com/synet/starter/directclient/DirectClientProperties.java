package com.synet.starter.directclient;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 网关服务器配置
 */
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "synet")
public class DirectClientProperties extends DirectClientConfig {
}
