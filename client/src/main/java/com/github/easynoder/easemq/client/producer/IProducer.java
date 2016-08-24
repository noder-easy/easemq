package com.github.easynoder.easemq.client.producer;

import com.github.easynoder.easemq.core.Message;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/24
 * E-mail:easynoder@outlook.com
 */
public interface IProducer {

    /**
     * send 消息, 暂不考虑异常情况
     *
     * @param topic
     * @param message
     */
    public void send(String topic, Message message);
}
