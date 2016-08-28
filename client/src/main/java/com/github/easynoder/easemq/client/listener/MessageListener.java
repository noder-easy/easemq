package com.github.easynoder.easemq.client.listener;

import com.github.easynoder.easemq.core.Message;

/**
 * Desc:
 * Author:easynoder
 * Date:16/8/28
 * E-mail:easynoder@outlook.com
 */
public interface MessageListener {

    //todo 消息监听器目前和topic绑定,后续可优化
    String getTopic();

    void onMessage(Message message);
}
