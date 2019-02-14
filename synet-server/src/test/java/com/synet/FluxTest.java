package com.synet;

import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * Flux 是一个0-N项的异步序列
 * Flux<T>是一个标准的Publisher<T>，它表示0到N个已发出的条目的异步序列，可以通过一个完成信号或一个错误来终止。
 * 在响应流规范中，这三种类型的信号转换为对下游订阅者（Subscriber’s ）的onNext、onComplete或onError方法的调用。
 * 由于可能的信号范围很大，所以Flux是通用的反应式。注意，所有事件，即使是终止事件，都是可选的:除了onComplete事件外，
 * 没有onNext事件表示一个空的有限序列，但是删除onComplete，您将得到一个无限的空序列(除了关于取消的测试之外，这并不是特别有用)。
 * 同样，无限序列不一定是空的。例如，Flux.interval(Duration)产生的Flux<Long>是无限的，它从时钟发出有规律的滴答声。
 */

public class FluxTest {

    /**
     * Lambda-based subscribe variants for Flux
     * subscribe(); 订阅并触发序列。
     * <p>
     * subscribe(Consumer<? super T> consumer);对每一个产生的价值做一些事情。
     * <p>
     * subscribe(Consumer<? super T> consumer,
     * Consumer<? super Throwable> errorConsumer);处理值，但也对错误作出反应。
     * <p>
     * subscribe(Consumer<? super T> consumer,
     * Consumer<? super Throwable> errorConsumer,
     * Runnable completeConsumer);处理值和错误，但也要在序列成功完成时执行一些代码。
     * <p>
     * subscribe(Consumer<? super T> consumer,
     * Consumer<? super Throwable> errorConsumer,
     * Runnable completeConsumer,
     * Consumer<? super Subscription> subscriptionConsumer);处理值、错误和成功完成，但也要处理此订阅调用生成的订阅。
     */


    @Test
    public void testFlux_Create() {
        Flux<String> seq1 = Flux.just("foo", "bar", "foobar");
        List<String> iterable = Arrays.asList("foo", "bar", "foobar");
        Flux<String> seq2 = Flux.fromIterable(iterable);
        Flux<Integer> numbersFromFiveToSeven = Flux.range(5, 3);
    }

    public String GetThreadId() {
        return " [tid:" + Thread.currentThread().getId() + "]";
    }

    @Test
    public void testFlux_1() {
        Flux<Integer> ints = Flux.range(1, 3);
        ints.subscribe(i -> {
            System.out.println(i + GetThreadId());
        });
    }

    @Test
    public void testFlux_2() {
        Flux<Integer> ints = Flux.range(1, 4)
                .map(i -> {
                    if (i <= 3) return i;
                    throw new RuntimeException("Got to 4");
                });
        ints.subscribe(i -> System.out.println(i + GetThreadId()),
                error -> System.err.println("Error: " + error + GetThreadId()));
    }

    @Test
    public void testFlux_3() {
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(i -> System.out.println(i + GetThreadId()),
                error -> System.err.println("Error " + error + GetThreadId()),
                () -> System.out.println("Done" + GetThreadId()));
    }

    @Test
    public void testFlux_4() {
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(i -> System.out.println(i + GetThreadId()),
                error -> System.err.println("Error " + error + GetThreadId()),
                () -> System.out.println("Done" + GetThreadId()),
                sub -> sub.request(10));
    }

    public class SampleSubscriber<T> extends BaseSubscriber<T> {

        public void hookOnSubscribe(Subscription subscription) {
            System.out.println("Subscribed" + GetThreadId());
            request(1);
        }

        public void hookOnNext(T value) {
            System.out.println(value + GetThreadId());
            request(1);
        }
    }

    @Test
    public void testFlux_5() {
        SampleSubscriber<Integer> ss = new SampleSubscriber<Integer>();
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(i -> System.out.println(i + GetThreadId()),
                error -> System.err.println("Error " + error + GetThreadId()),
                () -> System.out.println("Done" + GetThreadId()),
                s -> s.request(10));
        ints.subscribe(ss);
    }
}
