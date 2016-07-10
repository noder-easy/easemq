package com.github.easynoder.easemq.core;

import java.util.concurrent.BlockingQueue;

/**
 * Desc: Author:easynoder Date:16/7/10 E-mail:easynoder@outlook.com
 */
public class EaseMQueue<T> {

    // todo 简单的 生产者-消费者模型,共享内存模型
    private BlockingQueue<T> queue;

    public EaseMQueue(BlockingQueue<T> queue) throws Exception {
        this.queue = queue;
    }

    public BlockingQueue<T> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<T> queue) {
        this.queue = queue;
    }

}
