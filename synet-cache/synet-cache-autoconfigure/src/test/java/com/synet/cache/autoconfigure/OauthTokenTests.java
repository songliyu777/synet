package com.synet.cache.autoconfigure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OauthTokenTests {

    @Autowired
    private OauthTokenDao oauthTokenDao;

    @Test
    public void testSave() {
        OauthTokenEntity entity = new OauthTokenEntity();
        entity.setId(20000L);
        entity.setAccessToken("accesstoken20000");
        entity.setRefreshToken("refreshtoken20000");
        Mono<OauthTokenEntity> save = oauthTokenDao.save(entity);

        StepVerifier.create(save)
                .expectNext(entity)
                .verifyComplete();
    }

    @Test
    public void testFindByAccessToken() {
        OauthTokenEntity entity = new OauthTokenEntity();
        entity.setId(10000L);
        entity.setAccessToken("accesstoken10000");
        entity.setRefreshToken("refreshtoken10000");
        Mono<OauthTokenEntity> find = oauthTokenDao.findByAccessToken("accesstoken10000");

        StepVerifier.create(find)
                .expectNext(entity)
                .verifyComplete();
    }

    @Test
    public void testFindByRefreshToken() {
        OauthTokenEntity entity = new OauthTokenEntity();
        entity.setId(10000L);
        entity.setAccessToken("accesstoken10000");
        entity.setRefreshToken("refreshtoken10000");
        Mono<OauthTokenEntity> find = oauthTokenDao.findByRefreshToken("refreshtoken10000");

        StepVerifier.create(find)
                .expectNext(entity)
                .verifyComplete();
    }

    @Test
    public void testDelete10000() {
        OauthTokenEntity entity = new OauthTokenEntity();
        entity.setId(10000L);
        entity.setAccessToken("accesstoken10000");
        entity.setRefreshToken("refreshtoken10000");
        Mono<OauthTokenEntity> find = oauthTokenDao.deleteById(10000L);

        StepVerifier.create(find)
                .expectNext(entity)
                .verifyComplete();
    }

    @Test
    public void testFlux() {
        Flux<OauthTokenEntity> all = oauthTokenDao.findByAllTokens("all");
        StepVerifier.create(all)
                .expectNextCount(6L)
                .verifyComplete();
    }
}
