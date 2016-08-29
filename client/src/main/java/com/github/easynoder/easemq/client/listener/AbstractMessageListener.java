package com.github.easynoder.easemq.client.listener;

import com.github.easynoder.easemq.core.protocol.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/28
 * E-mail:easynoder@outlook.com
 */
public abstract class AbstractMessageListener implements MessageListener{

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessageListener.class);

    // 对应的topic
    private String topic;

    // 消费消息的线程数
    private int consumers = 2;

    // 默认最大消费消息的线程数
    private int maxConsumers = 5;

    // todo 线程池优化
    private ExecutorService executor ;

    public AbstractMessageListener(String topic) {
        this(topic, 2);
    }

    public AbstractMessageListener(String topic, int consumers) {
        this.topic = topic;
        this.consumers = consumers;
        if (consumers > maxConsumers) {
            this.consumers = maxConsumers;
        }
        if (consumers < 0) {
            this.consumers = 2;
        }
        executor = Executors.newFixedThreadPool(this.consumers);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void onMessage(final Message message) {
        executor.submit(new Runnable() {
            public void run() {
                try{
                    process(message);
                }catch (Exception e) {
                    LOGGER.error("process message error!!", e);
                }
            }
        });
    }

    public abstract void process(Message message);
}
