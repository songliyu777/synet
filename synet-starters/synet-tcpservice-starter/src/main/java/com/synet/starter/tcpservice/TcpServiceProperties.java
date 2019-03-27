package com.synet.starter.tcpservice;

import com.synet.net.tcp.TcpServiceConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 网关服务器配置
 *
 * @author konghang
 */
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "synet.tcpservice")
public class TcpServiceProperties extends TcpServiceConfig {

}
