package com.github.easynoder.easemq.client.listener;

import com.github.easynoder.easemq.core.protocol.GenerateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/28
 * E-mail:easynoder@outlook.com
 */
public class DefaultMessageListener extends AbstractMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMessageListener.class);

    public DefaultMessageListener(String topic) {
        super(topic);
    }

    public DefaultMessageListener(String topic, int consumers) {
        super(topic, consumers);
    }

    @Override
    public void process(GenerateMessage message) {
        LOGGER.info("process message, topic = {}, receive message = {}", this.getTopic(), message);
    }
}


