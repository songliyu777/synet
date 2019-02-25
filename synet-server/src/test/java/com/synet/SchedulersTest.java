package com.synet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import reactor.core.Disposable;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;

import static org.junit.Assert.assertTrue;

public class SchedulersTest {

    final Logger log = Loggers.getLogger(SchedulersTest.class);

    public String GetThreadId() {
        return " [tid:" + Thread.currentThread().getName() + "]";
    }

    @Test
    public void testThread() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        System.out.println("1:" + Thread.currentThread().getName());
        final Mono<String> mono = Mono.just("hello ");
        Thread t = new Thread(() -> mono
                .map(msg -> msg + "thread ")
                .subscribe(v -> {
                            System.out.println("2:" + v + Thread.currentThread().getName());
                            latch.countDown();
                        }
                )
        );
        t.start();
        t.join();//join会使线程顺序执行

        System.out.println("3:end testThread");

        assertTrue("Latch was counted down", latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testPublishOn() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);

        final Flux<String> flux = Flux
                .range(1, 2)
                .map(i -> { System.out.println("1:"+ i + GetThreadId());return 10 + i;}) //第一个线程中执行
                .publishOn(s)
                .map(i -> { System.out.println("2:"+ i + GetThreadId());return "value " + i;}); //scheduler 线程中执行

        new Thread(() -> flux.subscribe((str) -> {
            System.out.println("3:" + str + GetThreadId());//scheduler 线程中执行
            latch.countDown();
        })).start();

        assertTrue("Latch was counted down", latch.await(5, TimeUnit.SECONDS));
    }


    @Test
    public void testSubscribeOn() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Scheduler s = Schedulers.newParallel("parallel-scheduler", 4);

        final Flux<String> flux = Flux
                .range(1, 2)
                .map(i -> {
                    System.out.println("1:" + GetThreadId()); //scheduler 线程中执行
                    return 10 + i;
                })
                .subscribeOn(s)
                .map(i -> {
                    System.out.println("1:" + GetThreadId()); //scheduler 线程中执行
                    return "value " + i;
                });

        new Thread(() -> flux.subscribe((str) -> {
            System.out.println("3:" + str + GetThreadId());//scheduler 线程中执行
            latch.countDown();
        })).start();

        assertTrue("Latch was counted down", latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testHandErrors_1(){
        Flux.just(1, 2, 0)
                .map(i -> "100 / " + i + " = " + (100 / i)) //this triggers an error with 0
                .onErrorReturn("Divided by zero :("); // error handling example
    }

    /**
     * try {
     *     for (int i = 1; i < 11; i++) {
     *         String v1 = doSomethingDangerous(i);
     *         String v2 = doSecondTransform(v1);
     *         System.out.println("RECEIVED " + v2);
     *     }
     * } catch (Throwable t) {
     *     System.err.println("CAUGHT " + t);
     * }
     *
     * try {
     *   return doSomethingDangerous(10);
     * }
     * catch (Throwable error) {
     *   return "RECOVERED";
     * }
     *
     * */

    public String doSomethingDangerous(Integer s) {
        if(s.equals(10)){
             throw new Error("boom10");
        }
        return s.toString();
    }

    public String doSecondTransform(String s) {
            return s;
    }

    @Test
    public void testHandErrors_2(){
        Flux<String> s = Flux.range(1, 10)
                .map(v -> doSomethingDangerous(v))
                .map(v -> doSecondTransform(v));
        s.subscribe(value -> System.out.println("RECEIVED " + value),
                error -> System.err.println("CAUGHT " + error)
        );

        Flux.just(10)
                .map(this::doSomethingDangerous)
                .onErrorReturn("RECOVERED")
                .subscribe(value -> System.out.println("RECEIVED " + value),
                        error -> System.err.println("CAUGHT " + error));

        Flux.just(10)
                .map(this::doSomethingDangerous)
                .onErrorReturn(e -> e.getMessage().equals("boom10"), "recovered10")
                .subscribe(value -> System.out.println("RECEIVED " + value),
                        error -> System.err.println("CAUGHT " + error));
    }

    /**
     * String v1;
     * try {
     *   v1 = callExternalService("key1");
     * }
     * catch (Throwable error) {
     *   v1 = getFromCache("key1");
     * }
     *
     * String v2;
     * try {
     *   v2 = callExternalService("key2");
     * }
     * catch (Throwable error) {
     *   v2 = getFromCache("key2");
     * }
     * */

    public Flux<String> callExternalService(String s) {
        return Flux.just(s);
    }

    public Flux<String> getFromCache(String s) {
        return Flux.just(s);
    }


    @Test
    public void testHandErrors_3(){
        Flux.just("key1", "key2")
                .flatMap(k -> callExternalService(k)
                        .onErrorResume(e -> getFromCache(k))
                ).subscribe(System.out::println);
    }

    private class Stats {

        void startTimer() {
            System.out.println("startTimer" + GetThreadId());
        }

        void stopTimerAndRecordTiming() {
            System.out.println("stopTimerAndRecordTiming" + GetThreadId());
        }
    }

    @Test
    public void testDoFinally(){
        Stats stats = new Stats();
        LongAdder statsCancel = new LongAdder();

        Flux<String> flux =
                Flux.just("foo", "bar")
                        .doOnSubscribe(s -> stats.startTimer())
                        .doFinally(type -> {
                            stats.stopTimerAndRecordTiming();
                            if (type == SignalType.CANCEL)
                                statsCancel.increment();
                        })
                        .take(1);
        flux.subscribe(System.out::println);
    }

    @Test
    public void testDisposable(){
        AtomicBoolean isDisposed = new AtomicBoolean();
        Disposable disposableInstance = new Disposable() {
            @Override
            public void dispose() {
                isDisposed.set(true);
            }

            @Override
            public String toString() {
                return "DISPOSABLE";
            }
        };

        Flux<String> flux =
                Flux.using(
                        () -> disposableInstance,
                        disposable -> Flux.just(disposable.toString()),
                        Disposable::dispose
                );

        flux.subscribe(System.out::println);
    }

    @Test
    public void testInterval() throws InterruptedException {
        Flux<String> flux =
                Flux.interval(Duration.ofMillis(250))
                        .map(input -> {
                            if (input < 3) return "tick " + input;
                            throw new RuntimeException("boom");
                        })
                        .onErrorReturn("Uh oh");

        flux.subscribe((s)->System.out.println(s + GetThreadId()));
        Thread.sleep(2100);
    }

    @Test
    public void testRetrying() throws InterruptedException {
        Flux.interval(Duration.ofMillis(250))
                .map(input -> {
                    if (input < 3) return "tick " + input;
                    throw new RuntimeException("boom");
                })
                .retry(1)
                .elapsed() //打印计算每次调用的延时
                .subscribe(System.out::println, System.err::println);

        Thread.sleep(2100);
    }

    @Test
    public void testRetryWhen() throws InterruptedException {
        Flux<String> flux = Flux
                .<String>error(new IllegalArgumentException())
                .doOnError(System.err::println) // 这里打印错误
                .retryWhen(companion -> companion.take(3));

        flux.subscribe(System.out::println, System.out::println);

        flux = Flux.<String>error(new IllegalArgumentException())
                        .retryWhen(companion -> companion
                                .zipWith(Flux.range(1, 4),
                                        (error, index) -> {
                                            if (index < 4) return index;
                                            else throw Exceptions.propagate(error); // throw 之后下面才打印错误
                                        })
                        );

        flux.subscribe(System.out::println, System.err::println); //这里打印错误

        Flux.just("foo")
                .map(s -> { throw new IllegalArgumentException(s); })
                .subscribe(v -> System.out.println("GOT VALUE"), e -> System.out.println("ERROR: " + e));
        Thread.sleep(2100);
    }


    public String convert(int i) throws IOException {
        if (i > 3) {
            throw new IOException("boom " + i);
        }
        return "OK " + i;
    }

    @Test
    public void testRuntimeException() throws InterruptedException {
        Flux<String> converted = Flux
                .range(1, 10)
                .map(i -> {
                    try { return convert(i); }
                    catch (IOException e) { throw Exceptions.propagate(e); }
                });

        converted.subscribe(
                v -> System.out.println("RECEIVED: " + v),
                e -> {
                    if (Exceptions.unwrap(e) instanceof IOException) {
                        System.out.println("Something bad happened with I/O");
                    } else {
                        System.out.println("Something bad happened");
                    }
                }
        );
    }
}
