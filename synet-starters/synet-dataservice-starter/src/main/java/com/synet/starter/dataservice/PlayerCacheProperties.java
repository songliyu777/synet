package com.synet.starter.dataservice;

import com.synet.net.data.manager.PlayerCacheConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 网关服务器配置
 *
 * @author konghang
 */
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "synet.player.cache")
public class PlayerCacheProperties extends PlayerCacheConfig {

}
