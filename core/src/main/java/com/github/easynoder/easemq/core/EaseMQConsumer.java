package com.github.easynoder.easemq.core;

/**
 * Desc: Author:easynoder Date:16/7/10 E-mail:easynoder@outlook.com
 */
public class EaseMQConsumer<T> {

    private EaseMQueue<T> easeMQueue;

    public EaseMQConsumer(EaseMQueue<T> easeMQueue) {
        this.easeMQueue = easeMQueue;
    }

    public T consume() {
        try {
            return easeMQueue.getQueue().take();
        } catch (InterruptedException e) {
            return null;
        }
    }
}
