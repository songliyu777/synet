package com.yuyan.lightning.cache.autoconfigure;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class ReactorTests {

    @Test
    public void testFlux() {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        Mono<List<String>> listMono = Flux.fromIterable(list)
                .filter(item -> "b".equals(item)).collectList();
        listMono.subscribe(item -> {
            System.out.println(item);
        });
    }
}
