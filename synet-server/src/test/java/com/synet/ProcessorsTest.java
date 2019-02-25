package com.synet;

import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.UnicastProcessor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 使用 Sink Facade 安全处理多线程
 */

public class ProcessorsTest {

    public String GetThreadId() {
        return " [tid:" + Thread.currentThread().getName() + "]";
    }

    @Test
    public void testUnicastProcessor() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);

        Scheduler scheduler = Schedulers.parallel();

        UnicastProcessor<Integer> processor = UnicastProcessor.create();
        FluxSink<Integer> sink = processor.sink();

        sink.next(1).next(2).next(3);
        sink.complete();

        processor.publishOn(scheduler);

        processor.delaySubscription(Duration.ofMillis(1000)).subscribe((i) -> {
            System.out.println(i + GetThreadId());
            latch.countDown();
        });

        Assert.assertTrue("latch : ", latch.await(5, TimeUnit.SECONDS));

        scheduler.dispose();
    }

    @Test
    public void testEmitterProcessor() throws InterruptedException {
        Hooks.onOperatorDebug();
        CountDownLatch latch = new CountDownLatch(1);

        Scheduler scheduler = Schedulers.parallel();

        EmitterProcessor<Integer> processor = EmitterProcessor.create();
        FluxSink<Integer> sink = processor.sink();

        sink.next(1).next(2).next(3).complete();

        processor.publishOn(scheduler);

        processor.delaySubscription(Duration.ofMillis(1000)).subscribe((i) -> {
            System.out.println(i + GetThreadId());
            latch.countDown();
        });

        processor.doOnComplete(()->{System.out.println("doOnComplete" + GetThreadId());            latch.countDown();});

        Assert.assertTrue("latch : ", latch.await(5, TimeUnit.SECONDS));

        scheduler.dispose();
    }
}
