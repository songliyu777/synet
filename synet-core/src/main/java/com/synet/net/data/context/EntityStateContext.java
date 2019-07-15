package com.synet.net.data.context;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 实体状态上下文
 *
 * @author konghang
 */
public class EntityStateContext {

    private Long playerId;

    private List<StateEntityHolder> entities = Lists.newArrayList();

    private EntityStateContext() {

    }

    public List<StateEntityHolder> getEntities() {
        return entities;
    }

    /**
     * 玩家
     *
     * @param playerId
     * @return
     */
    public EntityStateContext palyer(Long playerId) {
        this.playerId = playerId;
        return this;
    }

    public Long getPlayerId() {
        return playerId;
    }

    /**
     * 保存
     * @param entity 实体
     */
    public EntityStateContext save(StateEntity entity) {
        entities.add(new StateEntityHolder(entity, StateEntityHolder.STATE_SAVE));
        return this;
    }

    /**
     * 保存
     * @param entities 实体
     */
    public EntityStateContext saveAll(List<StateEntity> entities) {
        List<StateEntityHolder> holders = entities.stream().map(item -> new StateEntityHolder(item, StateEntityHolder.STATE_SAVE)).collect(Collectors.toList());
        this.entities.addAll(holders);
        return this;
    }

    /**
     * 删除
     *
     * @param entity 实体
     */
    public EntityStateContext delete(StateEntity entity) {
        entities.add(new StateEntityHolder(entity, StateEntityHolder.STATE_DELETE));
        return this;
    }

    /**
     * 删除
     *
     * @param entities 实体
     */
    public EntityStateContext delete(List<StateEntity> entities) {
        List<StateEntityHolder> holders = entities.stream().map(item -> new StateEntityHolder(item, StateEntityHolder.STATE_DELETE)).collect(Collectors.toList());
        this.entities.addAll(holders);
        return this;
    }

    public final static EntityStateContext builder() {
        return new EntityStateContext();
    }
}
