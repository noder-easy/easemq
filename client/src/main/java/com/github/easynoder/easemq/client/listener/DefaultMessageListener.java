package com.github.easynoder.easemq.client.listener;

import com.github.easynoder.easemq.client.IMQClient;
import com.github.easynoder.easemq.core.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/28
 * E-mail:easynoder@outlook.com
 */
public class DefaultMessageListener implements MessageListener{

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageListener.class);

    // 对应的topic
    private String topic;

    private IMQClient client;

    public DefaultMessageListener(String topic) {
        this.topic = topic;
    }

    public DefaultMessageListener (String topic, IMQClient client) {
        this.topic = topic;
        this.client = client;
    }

    public String getTopic() {
        return topic;
    }

    public DefaultMessageListener setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public IMQClient getClient() {
        return client;
    }

    public void setClient(IMQClient client) {
        this.client = client;
    }

    public void onMessage(Message message) {
        // only print
        LOGGER.info("process message, topic = {}, receive message = {}", topic, message);
    }

}


