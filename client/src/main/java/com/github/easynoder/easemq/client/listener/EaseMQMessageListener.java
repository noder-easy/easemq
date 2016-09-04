package com.github.easynoder.easemq.client.listener;

import com.github.easynoder.easemq.core.protocol.GenerateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/28
 * E-mail:easynoder@outlook.com
 */
public class EaseMQMessageListener implements MessageListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(EaseMQMessageListener.class);

    // 对应的topic
    private String topic;

    // 消费消息的线程数,默认为2
    private int consumers = 2;

    // 默认最大消费消息的线程数
    private int maxConsumers = 5;

    private static final int DEFAULT_CONSUMER_SIZE = 2;

    private static final int DEFAULT_QUEUE_SIZE = 100000;

    private ExecutorService executor;

    private MessageListener ref;

    public EaseMQMessageListener(String topic) {
        this(topic, DEFAULT_CONSUMER_SIZE);
    }

    public EaseMQMessageListener(String topic, int consumers) {
        this(topic, consumers, null);
    }

    public EaseMQMessageListener(String topic, int consumers, MessageListener ref) {
        this.topic = topic;
        if (ref == null) {
            throw new IllegalArgumentException("messagelistener can't be null!");
        }
        this.ref = ref;

        this.consumers = consumers;
        if (consumers > maxConsumers) {
            this.consumers = maxConsumers;
        }
        if (consumers <= 0) {
            this.consumers = DEFAULT_CONSUMER_SIZE;
        }
        //TODO 可以继续优化
        executor = new ThreadPoolExecutor(
                this.consumers,
                this.maxConsumers,
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(DEFAULT_QUEUE_SIZE)

        );

        LOGGER.info("message listener init succ!");
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void onMessage(final GenerateMessage message) {
        this.executor.submit(new Runnable() {
            public void run() {
                try {
                    ref.onMessage(message);
                } catch (Exception e) {
                    LOGGER.error("consume message error!", e);
                }
            }
        });
    }

}
