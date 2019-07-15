package com.synet.net.data.mongo;

import com.synet.net.data.context.StateEntity;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * 响应式mongo数据访问层
 */
public abstract class AbstractReactiveMongoDao {

    /**
     * 保存
     *
     * @param operations
     * @param entity
     * @return
     */
    public <T extends StateEntity> Mono<T> save(ReactiveMongoOperations operations, T entity) {
        return operations.save(entity);
    }

    /**
     * 删除
     *
     * @param operations
     * @param entity
     * @return
     */
    public <T extends StateEntity> Mono<T> delete(ReactiveMongoOperations operations, T entity) {
        return operations.remove(entity).map(deleteResult -> entity);
    }

    /**
     * 根据玩家查找
     *
     * @param operations
     * @param playerKey
     * @param playerId
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends StateEntity> Flux<T> findAllByPlayerId(ReactiveMongoOperations operations, String playerKey, Long playerId, Class<T> clazz) {
        return operations.find(query(where(playerKey).is(playerId)), clazz);
    }

    /**
     * 查找玩家的某一个实体
     *
     * @param operations
     * @param id
     * @param clazz
     * @param <T>
     * @return
     */
    public <T extends StateEntity> Mono<T> findOneById(ReactiveMongoOperations operations, Long id, Class<T> clazz) {
        return operations.findById(id, clazz);
    }
}
