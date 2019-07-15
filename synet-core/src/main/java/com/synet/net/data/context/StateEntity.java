package com.synet.net.data.context;

/**
 * 状态实体接口
 *
 * 仅标识该实体可以放入实体状态上下文中
 *
 * @author konghang
 */
public interface StateEntity {
    /**
     * 获取主键
     *
     * @return id
     */
    Long getId();

//    /**
//     * 获取玩家id
//     *
//     * @return pid
//     */
//    Long getPlayerId();
}
