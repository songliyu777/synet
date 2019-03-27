package com.synet;

import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertThat;

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

    /**
     * SampleSubscriber类扩展了BaseSubscriber, BaseSubscriber是反应器中为用户定义的订阅者推荐的抽象类。
     * 该类提供可重写的钩子来优化订阅者的行为。默认情况下，它将触发一个无限制的请求，其行为与subscribe()完全相同。
     * 但是，当您需要自定义请求量时，扩展BaseSubscriber要有用得多。
     * */


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

    /**
     * BaseSubscriber还提供了一个requestUnbounded()方法来切换到unbounded模式(相当于request(Long.MAX_VALUE))，以及一个cancel()方法。
     * 它有额外的钩子:hookOnComplete、hookOnError、hookOnCancel和hookFinally(在序列终止时总是调用它，并将终止类型作为SignalType参数传入)
     * */

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

    @Test
    public void testFlux_6() {
        Flux.range(1, 10)
                .doOnRequest(r -> System.out.println("request of " + r))
                .subscribe(new BaseSubscriber<Integer>() {

                    @Override
                    public void hookOnSubscribe(Subscription subscription) {
                        request(1);
                    }

                    @Override
                    public void hookOnNext(Integer integer) {
                        System.out.println("Cancelling after having received " + integer);
                        cancel();
                    }
                });
    }

    /**
     * Synchronous generate
     * 这是用于同步和逐个排放的，这意味着sink是一个synchronioussink，它的next()方法在每次回调调用时最多只能被调用一次。
     * 然后可以另外调用error(Throwable)或complete()，但这是可选的。
     * */

    @Test
    public void testFlux_7() {
        Flux<String> flux = Flux.generate(
                () -> {System.out.println("callable" + GetThreadId()); return 0;},
                (state, sink) -> {
                    String tmp = "3 x " + state + " = " + 3*state;
                    sink.next(tmp);
                    if (state == 10) sink.complete();
                    return state + 1;
                });
        flux.subscribe((tmp)->System.out.println(tmp + GetThreadId()));
    }

    @Test
    public void testFlux_8() {
        Flux<String> flux = Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    String tmp = "3 x " + i + " = " + 3*i;
                    sink.next(tmp);
                    if (i == 10) sink.complete();
                    return state;
                });
        flux.subscribe((tmp)->System.out.println(tmp + GetThreadId()));
    }

    @Test
    public void testFlux_9() {
        Flux<String> flux = Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    String tmp = "3 x " + i + " = " + 3*i;
                    sink.next(tmp);
                    if (i == 10) sink.complete();
                    return state;
                }, (state) -> System.out.println("state: " + state + GetThreadId()));

        flux.subscribe((tmp)->System.out.println(tmp + GetThreadId()));
    }

    /**
     * Asynchronous & multi-threaded: create
     * create是一种更高级的通量编程创建形式，它适用于每轮的多个排放，甚至来自多个线程。
     * 它公开了一个FluxSink，以及它的next、error和complete方法。与generate相反，它没有基于状态的变体。
     * 另一方面，它可以在回调中触发多线程事件。
     * */

    interface MyEventListener<T> {
        void onDataChunk(List<T> chunk);
        void processComplete();
        void processError(Throwable e);
    }

    interface MyEventProcessor {
        void register(MyEventListener<String> eventListener);
        void fireEvents(String... values);
        void processComplete();
        void processError(Throwable a);
        void shutdown();
    }


    public static class ScheduledSingleListenerEventProcessor implements MyEventProcessor {
        private MyEventListener<String> eventListener;
        Scheduler executor = Schedulers.newParallel("com/synet/scheduler", 4);
        //Scheduler.Worker executor = Schedulers.newParallel(4,"com.synet.scheduler").createWorker();
        //private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        @Override
        public void register(MyEventListener<String> eventListener) {
            this.eventListener = eventListener;
        }

        @Override
        public void fireEvents(String... values) {
            //每个半秒发送一个事件
            executor.schedule(() -> eventListener.onDataChunk(Arrays.asList(values)),
                    500, TimeUnit.MILLISECONDS);
        }

        @Override
        public void processComplete() {
            executor.schedule(() -> eventListener.processComplete(),
                    500, TimeUnit.MILLISECONDS);
        }

        @Override
        public void processError(Throwable a){
            executor.schedule(() -> eventListener.processError(a),
                    500, TimeUnit.MILLISECONDS);
        }

        @Override
        public void shutdown() {
            this.executor.dispose();
        }
    }

    @Test
    public void testFlux_10() {
        MyEventProcessor myEventProcessor = new ScheduledSingleListenerEventProcessor();
        Flux.create(sink -> {
            myEventProcessor.register(
                    new MyEventListener<String>() {
                        public void onDataChunk(List<String> chunk) {
                            System.out.println("onDataChunk" + GetThreadId());
                            for (String s : chunk) {
                                if ("end".equalsIgnoreCase(s)) {
                                    sink.complete();
                                } else {
                                    sink.next(s);
                                }
                            }
                        }
                        public void processComplete() {
                            System.out.println("processComplete" + GetThreadId());
                            sink.complete();
                        }
                        public void processError(Throwable e) {
                            sink.error(e);
                        }
                    });
        }).log().subscribe((tmp)->System.out.println(tmp + GetThreadId()));
        myEventProcessor.fireEvents("1", "2", "3", "4", "end");
        try {
            //myEventProcessor.processComplete();
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myEventProcessor.shutdown();
        System.out.println("MainServer thread exit");
    }

    @Test
    public void testFlux_11() {
        MyEventProcessor myEventProcessor = new ScheduledSingleListenerEventProcessor();
        Flux<String> bridge = Flux.push(sink -> {
            myEventProcessor.register(
                    new MyEventListener<String>() {

                        public void onDataChunk(List<String> chunk) {
                            for(String s : chunk) {
                                sink.next(s);
                            }
                        }

                        public void processComplete() {
                            System.out.println("processComplete" + GetThreadId());
                            sink.complete();
                        }

                        public void processError(Throwable e) {
                            sink.error(e);
                        }
                    });
        });
        bridge.log().subscribe((tmp)->System.out.println(tmp + GetThreadId()));
        myEventProcessor.fireEvents("1", "2", "3", "4", "end");
        try {
            myEventProcessor.processError(new Error("test error"));
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myEventProcessor.shutdown();
        System.out.println("MainServer thread exit");
    }

    /**
     * push()或create()之后的清理
     * 两个回调onDispose和onCancel在取消或终止时执行任何清理。
     * onDispose可用于在通量完成、错误输出或取消时执行清理。
     * onCancel可用于在使用onDispose进行清理之前执行任何特定于cancel的操作。
     * */

    @Test
    public void testFlux_12() {
//        EmbeddedChannel channel = new EmbeddedChannel();
//        Flux<String> bridge = Flux.create(sink -> {
//            sink.onRequest(n -> channel.poll(n))
//                    .onCancel(() -> channel.cancel())
//                    .onDispose(() -> channel.close())
//        });
    }

    /**
     * The handle method is a bit different: it is an instance method, meaning that it is chained on an existing source (as are the common operators).
     * It is present in both Mono and Flux.
     *
     * handle 的不同在于可以跳过一些元素，不用
     *
     * */

    public String alphabet(int letterNumber) {
        if (letterNumber < 1 || letterNumber > 26) {
            return null;
        }
        int letterIndexAscii = 'A' + letterNumber - 1;
        return "" + (char) letterIndexAscii;
    }

    @Test
    public void testFlux_13() {
        Flux<String> alphabet = Flux.just(-1, 30, 13, 9, 20)
                .handle((i, sink) -> {
                    String letter = alphabet(i);
                    if (letter != null)
                        sink.next(letter);
                });

        alphabet.subscribe(System.out::println);
    }

}
