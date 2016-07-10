package com.github.easynoder.easemq.core;

import com.google.gson.Gson;

import com.github.easynoder.easemq.core.exception.StoreException;
import com.github.easynoder.easemq.core.store.IStore;
import com.github.easynoder.easemq.core.store.memory.DirectMemoryStore;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * Desc: Author:easynoder Date:16/7/10 E-mail:easynoder@outlook.com
 */
public class DirectMemoryStoreTest {


    private IStore<String> iStore;

    private final CountDownLatch latch = new CountDownLatch(10);

    private final Gson gson = new Gson();

    @Before
    public void init() {
        iStore = new DirectMemoryStore<String>(10);
        System.out.println("init succ...");
    }


    @Test
    public void test() {
        new Thread(new Runnable() {
            public void run() {

                while (true) {
                    int uid = RandomUtils.nextInt();
                    User user = new User(uid, "user_" + uid);
                    try {
                        iStore.store(gson.toJson(user));
                        Thread.sleep(2000l);
                        latch.countDown();
                    } catch (StoreException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "producer").start();

        new Thread(new Runnable() {
            public void run() {

                while (true) {
                    try {
                        String data = iStore.get();
                        System.out.println("get data: " + data);
                    } catch (StoreException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "consumer").start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @After
    public void teardown() {

        System.out.println("teardown...");
    }

}
