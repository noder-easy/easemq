package com.github.easynoder.easemq.client;

import com.github.easynoder.easemq.core.protocol.GenerateMessage;

/**
 * Desc: Author:easynoder Date:16/7/10 E-mail:easynoder@outlook.com
 */
public interface IMQClient {

    public void start();

    public void send(byte cmdType, GenerateMessage message);

    public Object sendAndGet(byte cmdType, GenerateMessage message);

    public void close();
}
