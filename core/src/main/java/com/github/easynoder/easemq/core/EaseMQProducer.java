package com.github.easynoder.easemq.core;

/**
 * Desc: Author:easynoder Date:16/7/10 E-mail:easynoder@outlook.com
 */
public class EaseMQProducer<T> {

    private EaseMQueue<T> easeMQueue;

    public EaseMQProducer(EaseMQueue<T> easeMQueue) {
        this.easeMQueue = easeMQueue;
    }

    public boolean produce(T data) {
        return this.easeMQueue.getQueue().offer(data);
    }


}
