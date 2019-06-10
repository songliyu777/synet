package com.synet.server.logic.login.database.dao;

import com.synet.cache.annotation.ReactiveCacheEvict;
import com.synet.cache.annotation.ReactiveCachePut;
import com.synet.cache.annotation.ReactiveCacheable;
import com.synet.server.logic.login.database.bean.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserDao {

    @Autowired
    private UserRepository userRepository;

    @ReactiveCachePut(value = "user", key = "#result.account")
    public Mono<User> save(User user) {
        return userRepository.save(user);
    }

    @ReactiveCacheable(value = "user", key = "#account")
    public Mono<User> findOne(String account) {
        return userRepository.findById(account);
    }

    @ReactiveCacheEvict(value = "user", key = "#account")
    public Mono<Void> delete(String account){
        return userRepository.deleteById(account);
    }
}

