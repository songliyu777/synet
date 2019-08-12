package com.synet.net.data.mongo;

import com.google.common.collect.Lists;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;

public class YuyanSimpleReactiveMongoRepository<T, ID extends Serializable> extends SimpleReactiveMongoRepository<T, ID> implements YuyanReactiveMongoRepository<T, ID>{

    @NonNull
    private final MongoEntityInformation<T, ID> entityInformation;
    @NonNull
    private final ReactiveMongoOperations mongoOperations;

    public YuyanSimpleReactiveMongoRepository(MongoEntityInformation<T, ID> entityInformation, ReactiveMongoOperations mongoOperations) {
        super(entityInformation, mongoOperations);
        this.entityInformation = entityInformation;
        this.mongoOperations = mongoOperations;
    }

    @Override
    public Flux<T> findListBy(Criteria criteria, Pageable pageable) {
        Query query = new Query(criteria).with(pageable);
        return mongoOperations.find(query, this.entityInformation.getJavaType(), this.entityInformation.getCollectionName());
    }

    @Override
    public Flux<T> findListBy(Criteria criteria) {
        Query query = new Query(criteria);
        return mongoOperations.find(query, this.entityInformation.getJavaType(), this.entityInformation.getCollectionName());
    }

    @Override
    public Mono<Long> countBy(Criteria criteria) {
        Query query = new Query(criteria);
        return mongoOperations.count(query, this.entityInformation.getCollectionName());
    }

    @Override
    public Mono<Page<T>> findPageBy(Criteria criteria, Pageable pageable) {
        Mono<Long> countMono = countBy(criteria);
        return countMono.flatMap(count -> {
            if (count > 0) {
                return findListBy(criteria, pageable).collectList().map(list -> new PageImpl<>(list, pageable, count));
            }
            return Mono.just(new PageImpl<>(Lists.newArrayList(), pageable, 0L));
        });
    }
}
