package com.synet.net.data.manager;

import com.synet.net.data.context.StateEntity;
import lombok.Data;

@Data
public class PlayerEntityConfig {
    /**
     * 实体类名（必须）
     */
    private String clazz;

    /**
     * 玩家字段标识（非必须，默认:playerId）
     */
    private String playerKey;

    /**
     * 缓存key（非必须，默认null）
     */
    private String cacheKey;

    /**
     * 获取实体class
     *
     * @return
     */
    public Class<? extends StateEntity> getEntityClass() {
        try {
            return (Class<? extends StateEntity>) Class.forName(this.clazz);
        } catch (ClassNotFoundException e) {
           throw new RuntimeException(e);
        }
    }
}
