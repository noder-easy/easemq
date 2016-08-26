package com.github.easynoder.easemq.server;

import com.github.easynoder.easemq.core.Message;

/**
 * Desc: Author:easynoder Date:16/7/10 E-mail:easynoder@outlook.com
 */
public interface IMQServer {

    public void start() throws InterruptedException;

    public void send(String topic, Message message);

    public void close();
}
