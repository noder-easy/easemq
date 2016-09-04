package com.github.easynoder.easemq.client.listener;

import com.github.easynoder.easemq.core.protocol.GenerateMessage;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/28
 * E-mail:easynoder@outlook.com
 */
public interface MessageListener {

    void onMessage(GenerateMessage message);
}
