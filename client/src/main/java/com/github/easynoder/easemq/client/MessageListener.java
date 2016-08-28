package com.github.easynoder.easemq.client;

import com.github.easynoder.easemq.core.Message;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/28
 * E-mail:easynoder@outlook.com
 */
public interface MessageListener {

    public void setClient(IMQClient client);

    public String getTopic();

    public void onMessage(Message message);
}
