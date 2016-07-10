package com.github.easynoder.easemq.core.store.memory;

import com.github.easynoder.easemq.core.exception.StoreException;
import com.github.easynoder.easemq.core.store.IStore;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 * Desc: Author:easynoder Date:16/7/10 E-mail:easynoder@outlook.com
 */
public class DirectMemoryStore<T> extends MemoryStore<T> implements IStore<T> {

    private BlockingQueue<T> queue;

    public static final int DEFAULT_QUEUE_SIZE = 10000;

    public static final int STORE_TIMEOUT = 5;

    public DirectMemoryStore(int queueCapacity) {
        if (queueCapacity < 0) {
            queueCapacity = DEFAULT_QUEUE_SIZE;
        }
        queue = new LinkedBlockingDeque<T>(queueCapacity);
    }

    @Override
    public boolean store(T data) throws StoreException {
        try {
            this.queue.put(data);
            return true;
        } catch (InterruptedException e) {
            throw new StoreException("store data failure! data: " + data);
        }
    }

    @Override
    public T get() throws StoreException {
        try {
            return this.queue.take();
        } catch (InterruptedException e) {
            throw new StoreException("get data failure!");
        }
    }
}
