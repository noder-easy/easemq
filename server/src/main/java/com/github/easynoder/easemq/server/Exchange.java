package com.github.easynoder.easemq.server;

import com.github.easynoder.easemq.core.protocol.GenerateMessage;

/**
 * Desc:
 * Author:easynoder
 * Date:16/9/6
 * E-mail:easynoder@outlook.com
 */
public interface Exchange {
    public void exchange(String routingKey, GenerateMessage message);
}
