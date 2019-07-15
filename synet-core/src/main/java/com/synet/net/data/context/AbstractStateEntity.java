package com.synet.net.data.context;

import lombok.Data;

/**
 * 抽象的状态实体
 */
@Data
public abstract class AbstractStateEntity {
    /**
     * 数据状态
     * 0：默认状态，新增实体
     * 1：修改实体
     * 2：删除实体
     */
    private int dataState;
}
