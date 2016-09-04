package com.github.easynoder.easemq.client.producer;

import com.github.easynoder.easemq.client.EaseMQClient;
import com.github.easynoder.easemq.client.IMQClient;
import com.github.easynoder.easemq.client.ZkManager;
import com.github.easynoder.easemq.client.listener.DefaultMessageListener;
import com.github.easynoder.easemq.client.listener.EaseMQMessageListener;
import com.github.easynoder.easemq.client.listener.MessageListenerAdapter;
import com.github.easynoder.easemq.core.protocol.CmdType;
import com.github.easynoder.easemq.core.protocol.GenerateMessage;

import java.util.UUID;

/**
 * Desc: 调用入口
 * Author:easynoder
 * Date:16/8/24
 * E-mail:easynoder@outlook.com
 */
public class DefaultProducer implements IProducer {

    private EaseMQClient client;

    private static final String topic = "easemq1";

    public DefaultProducer() {
        String zkAddr = "localhost:2181";
        ZkManager zkManager = new ZkManager();
        zkManager.setZkAddr(zkAddr);
        zkManager.init();

        MessageListenerAdapter listener = new EaseMQMessageListener(topic, 2, new DefaultMessageListener());
        client = new EaseMQClient();
        client.setTopic(topic);
        client.setListener(listener);
        client.setZkManager(zkManager);
        client.init();


    }

    public void send(String topic, GenerateMessage message) {
        client.send(message.getHeader().getTopic(), message);
    }


    public static void main(String[] args) {

        IProducer producer = new DefaultProducer();

        for (int i = 0; i < 1; i++) {
            GenerateMessage.Header header = new GenerateMessage.Header();
            header.setTopic(topic);
            header.setMessageId(UUID.randomUUID().toString());
            header.setTimestamp(System.currentTimeMillis());
            GenerateMessage message = new GenerateMessage();
            message.setBody("body" + i);
            message.setHeader(header);
            producer.send(topic, message);
        }
    }

}
