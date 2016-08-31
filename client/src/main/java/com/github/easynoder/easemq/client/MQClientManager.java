package com.github.easynoder.easemq.client;

import com.github.easynoder.easemq.client.listener.MessageListener;
import com.github.easynoder.easemq.core.protocol.GenerateMessage;

/**
 * Desc:
 * Author:easynoder
 * Date:16/7/10
 * E-mail:easynoder@outlook.com
 */
public class MQClientManager {

    public IMQClient imqClient = new NettyMQClient(new MessageListener() {
        public String getTopic() {
            // TODO: 16/8/31 使用zk消除listener对topic的依赖
            return null;
        }

        public void onMessage(GenerateMessage message) {

        }
    });


}
