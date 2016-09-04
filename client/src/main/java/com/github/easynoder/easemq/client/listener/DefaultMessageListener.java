package com.github.easynoder.easemq.client.listener;

import com.github.easynoder.easemq.core.protocol.GenerateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Desc:
 * Author:easynoder
 * Date:16/9/4
 * E-mail:easynoder@outlook.com
 */
public class DefaultMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageListener.class);

    public void onMessage(GenerateMessage message) {
        LOGGER.info("consume message = {}", message);
    }
}
