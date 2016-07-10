package com.github.easynoder.easemq.core;

import com.google.gson.Gson;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Desc: Author:easynoder Date:16/7/10 E-mail:easynoder@outlook.com
 */
public class EaseMQueueTest {

    private EaseMQProducer<String> producer;

    private EaseMQConsumer<String> consumer;

    private final CountDownLatch latch = new CountDownLatch(10);

    private final Gson gson = new Gson();

    @Before
    public void init() {
        BlockingQueue<String> queue = new LinkedBlockingDeque<String>(10);
        try {
            EaseMQueue<String> easeMQueue = new EaseMQueue<String>(queue);
            producer = new EaseMQProducer<String>(easeMQueue);
            consumer = new EaseMQConsumer<String>(easeMQueue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("init succ...");
    }

    @Test
    public void test() {

        new Thread(new Runnable() {
            public void run() {
                System.out.println("consumer thread start...");
                while (true) {
                    String data = consumer.consume();
                    System.out.println("consumer data:" + data);
                }
            }
        }).start();

        new Thread(new Runnable() {
            public void run() {

                System.out.println("producer thread start...");
                while (true) {
                    int id = RandomUtils.nextInt();
                    User user = new User(id, "user" + id);
                    boolean result = producer.produce(gson.toJson(user));
                    if (!result) {
                        System.out.println("produce error");
                        continue;
                    }
                    try {
                        TimeUnit.SECONDS.sleep(2L);
                        latch.countDown();
                        if (latch.getCount() <= 0) {
                            System.out.println("exit produce...");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("test end...");
    }

    @After
    public void teardown() throws InterruptedException {
        System.out.println("teardown...");
    }
}
