package com.github.easynoder.easemq.client;

import com.github.easynoder.easemq.client.listener.MessageListener;
import com.github.easynoder.easemq.core.Message;

/**
 * Desc: Author:easynoder Date:16/7/10 E-mail:easynoder@outlook.com
 */
public interface IMQClient {

    public void start();

    public void send(String topic, Message message);

    public void registeListener(MessageListener listener);

    public void close();
}
