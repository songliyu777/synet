package com.synet.net.data.manager;

import lombok.Data;

import java.util.List;

@Data
public class PlayerCacheConfig {

    /**
     * 缓存过期时间
     */
    private Long ttl;

    /**
     * 实体
     */
    private List<PlayerEntityConfig> entities;
}
