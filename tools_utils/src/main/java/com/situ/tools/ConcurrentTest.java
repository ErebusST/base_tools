/*
 * Copyright (c) 2023. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.tools;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程测试
 *
 * @author 司徒彬
 * @date 2023/6/7 11:19
 */
@Slf4j
public class ConcurrentTest {


    public static void begin(@Nonnull int threadCount, @Nonnull Runnable run) {
        begin(threadCount, run, null);
    }

    /**
     * Begin .
     *
     * @param threadCount the thread count
     * @param run         the run
     * @param finish      the finish
     * @author ErebusST
     * @since 2023 -06-07 11:36:18
     */
    public static void begin(int threadCount, Runnable run, Runnable finish) {
        if (ObjectUtils.isNull(run)) {
            return;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            pool.execute(() -> {
                try {
                    countDownLatch.await();
                    run.run();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        countDownLatch.countDown();
        pool.shutdown();
        while (!pool.isTerminated()) {
        }
        if (ObjectUtils.isNotNull(finish)) {
            finish.run();
        }
    }
}
