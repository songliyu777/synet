package com.synet.net.data.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@NoRepositoryBean
public interface YuyanReactiveMongoRepository<T, ID> extends ReactiveMongoRepository<T, ID> {

    /**
     * 查找list
     *
     * @param criteria 查询条件
     * @param pageable 分页
     * @return list
     */
    Flux<T> findListBy(Criteria criteria, Pageable pageable);

    /**
     * 查找list
     *
     * @param criteria 查询条件
     * @return list
     */
    Flux<T> findListBy(Criteria criteria);

    /**
     * 求数量
     *
     * @param criteria 查询条件
     * @return 数量
     */
    Mono<Long> countBy(Criteria criteria);

    /**
     * 分页
     *
     * @param criteria 查询条件
     * @param pageable 分页条件
     * @return 分页
     */
    Mono<Page<T>> findPageBy(Criteria criteria, Pageable pageable);
}
