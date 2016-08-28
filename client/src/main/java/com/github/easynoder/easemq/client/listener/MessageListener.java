package com.github.easynoder.easemq.client.listener;

import com.github.easynoder.easemq.client.IMQClient;
import com.github.easynoder.easemq.core.Message;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/28
 * E-mail:easynoder@outlook.com
 */
public interface MessageListener {

    void setClient(IMQClient client);

    String getTopic();

    void onMessage(Message message);
}
