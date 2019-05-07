package com.synet.server.logic.controller;

import com.synet.net.protobuf.mapping.*;
import com.synet.protobuf.TestOuterClass;
import com.synet.starter.feign.RemoteDelegation;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

class Player {
    public Player setboat(Boat b) {
        return this;
    }

}

class Boat {

}

@ProtobufController
public class TestController {

    @Autowired
    RemoteDelegation remoteDelegation;

    @ProtobufMapping(cmd = 1)
    public Mono<ProtoResponse> test(@Header ProtoHeader head, @Body TestOuterClass.Test test) {
        Player player = new Player();
        ProtoResponse response = ProtoResponse.builder().protoHeader(head).message(test).build();
        return Mono.just(player)
                .flatMap(p->Mono.just(new Boat()).map(b->player.setboat(b)))
                .flatMap(p->Mono.just(ProtoResponse.builder().protoHeader(head).message(test).build()));

        //remoteDelegation.query(head, test, head.getRemoteAddress()).subscribe();
        //return Mono.just(response);
        //return Mono.just(response).doOnSuccess((r) -> remoteDelegation.query(head, test, head.getRemoteAddress()).subscribe());
    }
}