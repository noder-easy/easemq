package com.github.easynoder.easemq.server;

import com.github.easynoder.easemq.core.protocol.GenerateMessage;

/**
 * Desc:
 * Author:easynoder
 * Date:16/9/6
 * E-mail:easynoder@outlook.com
 */
public interface Broker {

    public void broke(Exchange exchange, GenerateMessage message);
}
