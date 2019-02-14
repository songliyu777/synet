package com.synet;

import org.junit.Test;

/**
 * Flux 是一个0-N项的异步序列
 * Flux<T>是一个标准的Publisher<T>，它表示0到N个已发出的条目的异步序列，可以通过一个完成信号或一个错误来终止。
 * 在响应流规范中，这三种类型的信号转换为对下游订阅者（Subscriber’s ）的onNext、onComplete或onError方法的调用。
 * */

public class FluxTest {
    
    @Test
    public void testFlux() {
    }

}
