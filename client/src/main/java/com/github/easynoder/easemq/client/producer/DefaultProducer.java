package com.github.easynoder.easemq.client.producer;

import com.github.easynoder.easemq.client.IMQClient;
import com.github.easynoder.easemq.core.protocol.CmdType;
import com.github.easynoder.easemq.core.protocol.GenerateMessage;

/**
 * Desc: 调用入口
 * Author:easynoder
 * Date:16/8/24
 * E-mail:easynoder@outlook.com
 */
public class DefaultProducer implements IProducer {

    private IMQClient client;

    public DefaultProducer(IMQClient client) {
        this.client = client;
    }

    public void send(String topic, GenerateMessage message) {
        client.send(CmdType.CMD_STRING_MSG, message);
    }

}
